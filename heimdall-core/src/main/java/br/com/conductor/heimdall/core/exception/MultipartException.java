package br.com.conductor.heimdall.core.exception;

/**
 * This class represents the exception related to errors with MultipartFile
 *
 * @see HeimdallException
 */
public class MultipartException extends HeimdallException{

    /**
     * Creates a new Heimdall Exception.
     *
     * @param exceptionMessage {@link ExceptionMessage}
     */
    public MultipartException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage);
    }
}
