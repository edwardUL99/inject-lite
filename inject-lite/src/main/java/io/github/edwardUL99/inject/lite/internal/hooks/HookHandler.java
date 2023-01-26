package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

/**
 * Represents an object that can handle hooks on dependencies
 */
public interface HookHandler {
    /**
     * Handle the hook
     * @param injector the injector instance
     * @param instance the instance of the object. Can be null at certain stages
     * @param cls the class of the dependency
     */
    void handle(InternalInjector injector, Object instance, Class<?> cls);
}
