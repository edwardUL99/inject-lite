package io.github.edwardUL99.inject.lite.internal.fields;

/**
 * This interface defines an object that can inject objects into fields annotated with @Inject
 */
public interface FieldInjector {
    /**
     * Inject any @Field annotated field of the provided object
     * @param obj the object to inject
     */
    void injectFields(Object obj);
}
