package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.injector.Injector;

/**
 * Creates default injectors
 */
public class DefaultInjectorFactory implements InjectorFactory {
    @Override
    public Injector create() {
        return new DefaultInjector<>(
                new DelayedInjectableDependency.Factory()
        );
    }
}
