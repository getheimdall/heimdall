
package br.com.conductor.heimdall.core.exception;

/**
 * Classe que representa as exceções relacionadas timeout.
 * 
 * @author Filipe Germano <filipe.germano@conductor.com.br>
 *
 */
public class TimeoutException extends HeimdallException{

     private static final long serialVersionUID = -7899837702020219372L;

     public TimeoutException(ExceptionMessage exeptionMessage){
          super(exeptionMessage);
     }
}
