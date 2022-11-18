package io.github.edwardUL99.inject.lite.internal.injector;

/**
 * Represents a dependency to be injected by an injector
 */
public interface InjectableDependency {
    /**
     * Get the name of the dependency
     * @return the dependency name
     */
    String getName();

    /**
     * Determines if the dependency is a singleton or not
     * @return true if singleton, false if not
     */
    boolean isSingleton();

    /**
     * Get the type of the dependency
     * @return the dependency type
     */
    Class<?> getType();

    /**
     * Get the dependency instance
     * @return the instance of the dependency
     */
    Object get();

    /**
     * Get a dependency the same as this one but with a different name
     * @param name the different name
     * @return the new dependency
     */
    InjectableDependency withDifferentName(String name);
}
