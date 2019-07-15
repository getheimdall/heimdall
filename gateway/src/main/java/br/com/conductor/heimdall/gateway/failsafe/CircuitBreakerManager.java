/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==========================LICENSE_END===================================
 */
package br.com.conductor.heimdall.gateway.failsafe;

import br.com.conductor.heimdall.core.environment.Property;
import br.com.conductor.heimdall.core.trace.TraceContextHolder;
import br.com.conductor.heimdall.gateway.util.ConstantsContext;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Circuit Breaker handler for Operations and calls made inside middlewares.
 *
 * @author Marcos Filho
 * @author Marcelo Rodrigues
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Component
@Slf4j
public class CircuitBreakerManager {
	
	private final Property property;

	private static final ConcurrentHashMap<String, CircuitBreakerHolder> circuits = new ConcurrentHashMap<>();

	public CircuitBreakerManager(Property property) {
		this.property = property;
	}

	public <T> T failsafe(Callable<T> callable, String operationId, String operationPath) {
		CircuitBreakerHolder circuitBreakerHolder = getCircuitHolder(operationId);
		CircuitBreaker circuitBreaker = circuitBreakerHolder.getCircuitBreaker();
		
		if (circuitBreaker.isOpen()) {
			return Failsafe.with(circuitBreaker)
					.withFallback(() ->  {
						String body = logAndCreateBody(operationPath, circuitBreakerHolder.getMessage());

						RequestContext context = RequestContext.getCurrentContext();
						context.setSendZuulResponse(false);
						context.setResponseStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
						context.setResponseBody(body);
						context.addZuulResponseHeader(ConstantsContext.CIRCUIT_BREAKER_ENABLED, "enabled");
						context.getResponse().setContentType(MediaType.APPLICATION_JSON_VALUE);
					})
					.get(callable);
		}

		return Failsafe.with(circuitBreaker)
				.onFailure((ignored, throwable) -> circuitBreakerHolder.setThrowable(throwable))
				.get(callable);
	}

	private CircuitBreakerHolder getCircuitHolder(String key) {

		CircuitBreakerHolder breakerHolder = circuits.get(key);

		if (Objects.isNull(breakerHolder)) {
			breakerHolder = new CircuitBreakerHolder();
			breakerHolder.setCircuitBreaker(new CircuitBreaker()
					.withFailureThreshold(property.getFailsafe().getFailureNumber())
					.withSuccessThreshold(property.getFailsafe().getSuccessNumber())
					.withDelay(property.getFailsafe().getDelayTimeSeconds(), TimeUnit.SECONDS));

			circuits.put(key, breakerHolder);
		}

		return breakerHolder;
	}

	private String logAndCreateBody(String... args) {
		String finalMessage = new MessageFormat("CircuitBreaker ENABLED | Operation: {0}, Exception: {1}").format(args);

		log.info(finalMessage);
		TraceContextHolder.getInstance().getActualTrace().trace("CircuitBreaker Enabled" , finalMessage);

		return "{" +
				"\"" + HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase() + "\": \"" + args[0] + "\"," +
				"\"message\": \"" + args[1] + "\"" +
				"}";
	}

}
