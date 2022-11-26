package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraphAware;

/**
 * This interface defines an object that can inject objects into fields annotated with @Inject
 */
public interface FieldInjector extends DependencyGraphAware {
    /**
     * Inject any @Field annotated field of the provided object
     * @param obj the object to inject
     */
    void injectFields(Object obj);
}
