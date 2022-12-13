package io.github.edwardUL99.inject.lite.internal.methods;

import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraphAware;

/**
 * Injects setter methods annotated with Inject annotation
 */
public interface MethodInjector extends DependencyGraphAware {
    /**
     * Inject the methods of the given object if it has any
     * @param name the name of the dependency
     * @param object the object to inject
     */
    void injectMethods(String name, Object object);
}
