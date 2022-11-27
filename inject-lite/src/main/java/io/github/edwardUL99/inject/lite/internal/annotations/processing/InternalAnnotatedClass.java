package io.github.edwardUL99.inject.lite.internal.annotations.processing;

import io.github.edwardUL99.inject.lite.annotations.processing.AnnotatedClass;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;

/**
 * The internal API for the annotated class object
 * @param <T> the type of the object
 */
public class InternalAnnotatedClass<T> extends AnnotatedClass<T> {
    /**
     * Create an instance
     * @param annotation the annotation
     * @param dependency the dependency object
     */
    public InternalAnnotatedClass(T annotation, InjectableDependency dependency) {
        super(annotation, dependency);
    }

    /**
     * Retrieve the dependency within the processing package
     * @return the dependency
     */
    public InjectableDependency getInjectableDependency() {
        return dependency;
    }

    /**
     * Set the dependency instance
     * @param dependency the new dependency instance
     */
    public void setInjectableDependency(InjectableDependency dependency) {
        this.dependency = dependency;
    }
}
