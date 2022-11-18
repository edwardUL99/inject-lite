package io.github.edwardUL99.inject.lite.exceptions;

/**
 * This exception is thrown when the type of the expected dependency for a given name does
 * not match the type of the registered dependency
 */
public class DependencyMismatchException extends InjectionException {
    /**
     * Create the exception
     * @param name name of the dependency
     * @param expected the expected type of the exception
     * @param actual the actual type of the exception
     */
    public DependencyMismatchException(String name, Class<?> expected, Class<?> actual) {
       super(String.format("Expected type of dependency %s is %s, but the registered type is %s", name, expected, actual));
    }
}
