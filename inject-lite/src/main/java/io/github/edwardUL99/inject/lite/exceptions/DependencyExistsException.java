package io.github.edwardUL99.inject.lite.exceptions;

/**
 * An exception that is thrown if we attempt to register a duplicate dependency
 */
public class DependencyExistsException extends InjectionException {
    /**
     * Create the exception with the name of the existing dependency
     * @param name the name of the dependency
     */
    public DependencyExistsException(String name) {
        super(String.format("Dependency with name %s already exists", name));
    }
}
