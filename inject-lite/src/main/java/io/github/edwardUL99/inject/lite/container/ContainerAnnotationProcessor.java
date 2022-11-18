package io.github.edwardUL99.inject.lite.container;

import io.github.edwardUL99.inject.lite.annotations.processing.AnnotatedClass;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationProcessor;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Represents an annotation processor that can be used in the context of containers. Delegates
 * to the wrapped processor
 */
public class ContainerAnnotationProcessor<T extends Annotation> implements AnnotationProcessor<T> {
    /**
     * The annotation class
     */
    private final Class<T> annotation;
    /**
     * The processor to register in the container
     */
    private final AnnotationProcessor<T> processor;

    /**
     * Create the processor
     * @param annotation the annotation to scan
     * @param processor the processor to register
     */
    public ContainerAnnotationProcessor(Class<T> annotation, AnnotationProcessor<T> processor) {
        this.annotation = annotation;
        this.processor = processor;
    }

    /**
     * Get the wrapped processor
     * @return wrapped processor instance
     */
    public AnnotationProcessor<T> getProcessor() {
        return processor;
    }

    /**
     * Get the annotation being scanned
     * @return the annotation class
     */
    public Class<T> getAnnotation() {
        return annotation;
    }

    @Override
    public void process(List<AnnotatedClass<T>> classes) {
        processor.process(classes);
    }
}
