package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

/**
 * A factory class for creating CommonDependencyHandlers
 */
public class DependencyHandlerFactory {
    /**
     * Get a common dependency handler instance for the provided injector
     * @param injector the injector instance
     * @return the instantiated instance
     */
    public static CommonDependencyHandler getDependencyHandler(InternalInjector injector) {
        return new CommonDependencyHandlerImproved(injector);
    }
}
