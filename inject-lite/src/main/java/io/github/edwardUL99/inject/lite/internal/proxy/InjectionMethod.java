package io.github.edwardUL99.inject.lite.internal.proxy;

import io.github.edwardUL99.inject.lite.exceptions.InjectionException;

/**
 * Represents a method that can inject a dependency
 */
@FunctionalInterface
public interface InjectionMethod {
    /**
     * Inject the dependency
     * @return the injected dependency
     * @throws InjectionException if any error occurs
     */
    Object inject() throws InjectionException;
}
