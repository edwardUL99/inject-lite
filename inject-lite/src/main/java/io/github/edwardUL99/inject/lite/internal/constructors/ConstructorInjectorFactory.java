package io.github.edwardUL99.inject.lite.internal.constructors;

import io.github.edwardUL99.inject.lite.injector.Injector;

/**
 * A factory for created a constructor injector
 */
public final class ConstructorInjectorFactory {
    private ConstructorInjectorFactory() {}

    /**
     * Get a constructor injector instance using the provided injector
     * @param injector the injector to use for injecting dependencies into the constructor
     * @return the instance
     */
    public static ConstructorInjector getConstructorInjector(Injector injector) {
        return new DefaultConstructorInjector(injector);
    }
}
