package io.github.edwardUL99.inject.lite.exceptions;

/**
 * Represents an exception thrown by the containers package
 */
public class ContainerException extends AsyncException {
    /**
     * Construct the exception
     * @param message exception message
     * @param cause cause
     */
    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct the exception
     * @param message exception message
     */
    public ContainerException(String message) {
        super(message);
    }
}
