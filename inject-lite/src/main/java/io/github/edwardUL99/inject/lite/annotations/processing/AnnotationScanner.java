package io.github.edwardUL99.inject.lite.annotations.processing;

import java.lang.annotation.Annotation;

/**
 * This interface represents an object that can scan for annotations and apply registered processors to the annotated class
 */
public interface AnnotationScanner {
    /**
     * Register the provided processor for the given annotation. Multiple processors for the same annotation can be
     * registered
     * @param annotation the annotation to scan for and apply the processors to classes annotated with that
     *                   annotation
     * @param processor the processor to apply to classes annotated with this annotation
     * @param <T> the annotation type
     */
    <T extends Annotation> void registerAnnotationProcessor(Class<T> annotation, AnnotationProcessor<T> processor);

    /**
     * Scan for all the registered annotations and apply their associated processors
     */
    void scan();

    /**
     * Scan for classes annotated with the specified annotation and apply its processors
     * @param annotation the annotation to scan for
     * @param <T> the type of the annotation
     */
    <T extends Annotation> void scan(Class<T> annotation);
}
