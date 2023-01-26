package io.github.edwardUL99.inject.lite.exceptions;

/**
 * An exception thrown from the context of hook invocations
 */
public class HookException extends InjectionException {
    /**
     * Create an exception with the provided message
     * @param message the message for the exception
     */
    public HookException(String message) {
        super(message);
    }

    /**
     * Create an exception with the provided message and cause
     * @param message the message for the exception
     * @param cause the cause of the exception
     */
    public HookException(String message, Throwable cause) {
        super(message, cause);
    }
}
