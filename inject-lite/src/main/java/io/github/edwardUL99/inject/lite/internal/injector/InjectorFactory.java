package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.injector.Injector;

/**
 * A factory to create instances of injectors
 */
public interface InjectorFactory {
    /**
     * Create the injector and return it
     * @return the injector instance
     */
    Injector create();
}
