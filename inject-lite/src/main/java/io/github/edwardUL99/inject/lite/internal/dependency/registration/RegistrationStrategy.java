package io.github.edwardUL99.inject.lite.internal.dependency.registration;

import io.github.edwardUL99.inject.lite.injector.Injector;

/**
 * A strategy to register dependencies
 */
public interface RegistrationStrategy {
    /**
     * Register the dependency
     * @param injector the injector to register the dependency to
     */
    void register(Injector injector);
}
