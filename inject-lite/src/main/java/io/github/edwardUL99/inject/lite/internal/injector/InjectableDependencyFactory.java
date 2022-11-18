package io.github.edwardUL99.inject.lite.internal.injector;

/**
 * Factory for creating injectable dependency instances
 * @param <D> the type of the dependency
 */
public interface InjectableDependencyFactory<D extends InjectableDependency> {
    /**
     * Create the dependency
     * @param name the name of the dependency
     * @param type the type of the dependency
     * @param injector the injector instantiating the dependency
     * @param singleton true if singleton, false if not
     * @return the instance
     */
    D instantiate(String name, Class<?> type, InternalInjector<D> injector, boolean singleton);
}
