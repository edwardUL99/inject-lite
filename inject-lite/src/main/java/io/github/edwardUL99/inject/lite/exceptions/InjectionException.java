package io.github.edwardUL99.inject.lite.exceptions;

/**
 * The base exception for all injection related issues
 */
public class InjectionException extends RuntimeException {
    /**
     * Create an injection exception with the message
     * @param message the exception message
     */
    public InjectionException(String message) {
        super(message);
    }

    /**
     * Create an injection exception with the message and cause
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public InjectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
