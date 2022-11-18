package io.github.edwardUL99.inject.lite.exceptions;

/**
 * An exception to throw when a dependency is not found
 */
public class DependencyNotFoundException extends InjectionException {
    /**
     * When searching for a named dependency, use this constructor
     * @param name the name of the dependency
     */
    public DependencyNotFoundException(String name) {
        super(String.format("No dependency found with name %s", name));
    }

    /**
     * When finding a dependency by type, use this constructor
     * @param cls the class of the dependency we are searching for
     * @param <T> the type of the class
     */
    public <T> DependencyNotFoundException(Class<T> cls) {
        super(String.format("No dependency matching type %s found", cls));
    }
}
