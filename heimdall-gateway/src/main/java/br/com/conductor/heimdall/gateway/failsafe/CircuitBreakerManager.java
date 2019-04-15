/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
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
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.conductor.heimdall.gateway.util.ConstantsContext;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
 */
@Component
@Slf4j
public class CircuitBreakerManager {
	
	@Autowired
	private Property property;

	private static final ConcurrentHashMap<Long, CircuitBreakerHolder> circuits = new ConcurrentHashMap<>();

	private static final ConcurrentHashMap<String, CircuitBreakerHolder> middlewareCircuits = new ConcurrentHashMap<>();

	public <T> T failsafe(Callable<T> callable, Long operationId, String operationPath) {
		CircuitBreakerHolder circuitBreakerHolder = getCircuitHolder(operationId, circuits);
		CircuitBreaker circuitBreaker = circuitBreakerHolder.getCircuitBreaker();
		
		if (circuitBreaker.isOpen()) {
			return Failsafe.with(circuitBreaker)
					.withFallback(() ->  {
						String body = logAndCreateBody("CircuitBreaker ENABLED | Operation: {0}, Exception: {1}",
								operationPath,
								circuitBreakerHolder.getThrowable().getCause().getMessage());

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

	public <T> T failsafe(Callable<T> callable, String url) {
		CircuitBreakerHolder circuitBreakerHolder = getCircuitHolder(url, middlewareCircuits);
		CircuitBreaker circuitBreaker = circuitBreakerHolder.getCircuitBreaker();

		if (circuitBreaker.isOpen()) {
			return Failsafe.with(circuitBreaker)
					.withFallback(() -> {
						String body = logAndCreateBody("CircuitBreaker ENABLED | URL: {0}, Exception: {1}",
								url,
								circuitBreakerHolder.getThrowable().getMessage());

						return ResponseEntity
								.status(HttpStatus.SERVICE_UNAVAILABLE.value())
								.header(ConstantsContext.CIRCUIT_BREAKER_ENABLED, "enabled")
								.body(body);

					}).get(callable);
		}

		return Failsafe.with(circuitBreaker)
				.onFailure((ignored, throwable) -> circuitBreakerHolder.setThrowable(throwable))
				.get(callable);
	}

	private <T> CircuitBreakerHolder getCircuitHolder(T key, ConcurrentHashMap<T, CircuitBreakerHolder> concurrentHashMap) {

		CircuitBreakerHolder breakerHolder = concurrentHashMap.get(key);

		if (Objects.isNull(breakerHolder)) {
			breakerHolder = new CircuitBreakerHolder();
			breakerHolder.setCircuitBreaker(new CircuitBreaker()
					.withFailureThreshold(property.getFailsafe().getFailureNumber())
					.withSuccessThreshold(property.getFailsafe().getSucessNumber())
					.withDelay(property.getFailsafe().getDelayTimeSeconds(), TimeUnit.SECONDS));

			concurrentHashMap.put(key, breakerHolder);
		}

		return breakerHolder;
	}

	private String logAndCreateBody(String message, String... args) {
		String finalMessage = new MessageFormat(message).format(args);

		log.info(finalMessage);
		TraceContextHolder.getInstance().getActualTrace().trace("CircuitBreaker Enabled" , finalMessage);

		return "{" +
				"\"" + HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase() + "\": \"" + args[0] + "\"," +
				"\"message\": \"" + args[1] + "\"" +
				"}";
	}

}
