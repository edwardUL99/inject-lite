package io.github.edwardUL99.inject.lite.annotations.processing;

import io.github.edwardUL99.inject.lite.internal.injector.InjectableDependency;

/**
 * Represents a class annotated with the specific annotation
 * @param <T> the type of the annotation
 */
public abstract class AnnotatedClass<T> {
    /**
     * The dependency representing the instance that is about to be injected
     */
    protected InjectableDependency dependency;
    /**
     * The annotation annotated on the class
     */
    protected final T annotation;

    /**
     * Create an instance of the annotated class
     * @param annotation the annotation
     * @param dependency the proxy that will perform injection on object retrieval
     */
    public AnnotatedClass(T annotation, InjectableDependency dependency) {
        this.dependency = dependency;
        this.annotation = annotation;
    }

    /**
     * The class object annotated
     * @return annotated class
     */
    public Class<?> getAnnotatedClass() {
        return dependency.getType();
    }

    /**
     * Get the annotation annotated on the class
     * @return the annotated class
     */
    public T getAnnotation() {
        return annotation;
    }

    /**
     * Instantiate the annotated class, injecting it with any dependencies
     * @return the instantiated class
     */
    public Object instantiate() {
        return dependency.get();
    }
}
