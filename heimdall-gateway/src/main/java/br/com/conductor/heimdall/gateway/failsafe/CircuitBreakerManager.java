package br.com.conductor.heimdall.gateway.failsafe;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.conductor.heimdall.core.environment.Property;
import br.com.conductor.heimdall.core.exception.CircuitBreakerException;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;

/**
 * 
 * @author marcos.filho
 *
 */
@Component
public class CircuitBreakerManager {
	
	@Autowired
	private Property property;

	private static final ConcurrentHashMap<Long, CircuitBreaker> circuits = new ConcurrentHashMap<Long, CircuitBreaker>();

	public <T> T failsafe(Callable<T> callable, Long operationId) {

		CircuitBreaker circuitBreaker = getCircuit(operationId);
		
		if (circuitBreaker.isOpen()) {
			return Failsafe.with(circuitBreaker).withFallback(() -> {
				throw new CircuitBreakerException(ExceptionMessage.CIRCUIT_BREAK_ACTIVE);
			}).get(callable);
		}

		return Failsafe.with(circuitBreaker).get(callable);
	}

	private CircuitBreaker getCircuit(Long operationId) {

		CircuitBreaker breaker = circuits.get(operationId);

		if (Objects.isNull(breaker)) {
			CircuitBreaker circuitBreaker = new CircuitBreaker()
					.withFailureThreshold(property.getFailsafe().getFailureNumber())
					.withSuccessThreshold(property.getFailsafe().getSucessNumber())
					.withDelay(property.getFailsafe().getDelayTimeSeconds(), TimeUnit.SECONDS);
			circuits.put(operationId, circuitBreaker);
			breaker = circuitBreaker;
		}
		
		return breaker;
	}

}
