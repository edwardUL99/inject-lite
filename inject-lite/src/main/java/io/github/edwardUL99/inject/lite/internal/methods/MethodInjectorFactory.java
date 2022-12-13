package io.github.edwardUL99.inject.lite.internal.methods;

import io.github.edwardUL99.inject.lite.injector.Injector;

/**
 * A factory for method injectors
 */
public final class MethodInjectorFactory {
    // prevent instantiation
    private MethodInjectorFactory() {}

    /**
     * Create and return a method injector for the provided injector
     * @param injector the injector to inject dependencies with
     * @return the method injector instance
     */
    public static MethodInjector getMethodInjector(Injector injector) {
        return new DefaultMethodInjector(injector);
    }
}
