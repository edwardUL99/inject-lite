package io.github.edwardUL99.inject.lite.exceptions;

/**
 * Exception thrown when a circular dependency is detected
 */
public class CircularDependencyException extends InjectionException {
    /**
     * Create the exception with the provided message
     * @param message the message for the exception
     */
    public CircularDependencyException(String message) {
        super(message);
    }
}
