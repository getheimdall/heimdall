package br.com.conductor.heimdall.core.exception;

/**
 * This class represents the exception related to Circuit Breaker
 * @author marcos.filho
 *
 */
public class CircuitBreakerException extends ServerErrorException {

	private static final long serialVersionUID = -5348358333985021982L;

	public CircuitBreakerException(ExceptionMessage exeptionMessage) {
		super(exeptionMessage);
	}
}
