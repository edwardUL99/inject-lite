package io.github.edwardUL99.inject.lite.annotations.processing;

import java.util.List;

/**
 * This interface provides a functional interface which accepts a list of classes annotated with a specified annotation.
 * The library will scan for these annotations and pass the list into the processor
 * @param <T> the annotation type
 */
@FunctionalInterface
public interface AnnotationProcessor<T> {
    /**
     * Process the list of annotated classes annotated with annotation T. Only called if the size of the list is greater
     * than 0
     * @param classes the list of annotated classes
     */
    void process(List<AnnotatedClass<T>> classes);
}
