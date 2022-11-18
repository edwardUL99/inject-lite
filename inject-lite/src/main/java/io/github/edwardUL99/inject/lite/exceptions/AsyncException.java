package io.github.edwardUL99.inject.lite.exceptions;

public class AsyncException extends InjectionException {
    /**
     * Construct the exception
     * @param message exception message
     * @param cause cause
     */
    public AsyncException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct the exception
     * @param message exception message
     */
    public AsyncException(String message) {
        super(message);
    }
}
