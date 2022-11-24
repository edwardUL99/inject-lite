package io.github.edwardUL99.inject.lite.exceptions;

/**
 * An exception thrown if ambiguous dependency detection is enabled and is encountered
 */
public class AmbiguousDependencyException extends InjectionException {
    /**
     * Create the exception with the ambiguous class
     * @param cls the class that is ambiguous
     */
    public AmbiguousDependencyException(Class<?> cls) {
        super("Multiple dependencies match " + cls + " and require named dependencies if multiple match is enabled");
    }
}
