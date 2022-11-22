package io.github.edwardUL99.inject.lite.internal.constructors;

import io.github.edwardUL99.inject.lite.internal.dependency.DependencyGraphAware;

/**
 * Defines an object that can inject constructors annotated with @Inject.
 * If no such constructor is found, it attempts to find a no-arg constructor and just instantiates the object.
 * If more than one inject constructor is found, an exception is thrown
 */
public interface ConstructorInjector extends DependencyGraphAware {
    /**
     * Inject the constructor of the provided class. If no constructor with Inject is found, a no-arg will be looked up
     * @param name the name of the dependency
     * @param cls the class to be injected
     * @return the injected instance
     */
    Object injectConstructor(String name, Class<?> cls);
}
