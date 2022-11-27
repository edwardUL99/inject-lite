package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

/**
 * Factory for creating injectable dependency instances
 */
public interface InjectableDependencyFactory {
    /**
     * Create the dependency
     * @param name the name of the dependency
     * @param type the type of the dependency
     * @param injector the injector instantiating the dependency
     * @param singleton true if singleton, false if not
     * @return the instance
     */
    InjectableDependency instantiate(String name, Class<?> type, InternalInjector injector, boolean singleton);
}
