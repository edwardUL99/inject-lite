package io.github.edwardUL99.inject.lite.annotations.processing;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Utility functions around the annotations processing API. Each utility method creates a new scanner, and then scans for the
 * annotation
 */
public final class AnnotationsProcessing {
    /**
     * Get the scanner instance
     * @return the annotation scanner
     */
    private static AnnotationScanner getScanner() {
        return AnnotationScanners.newScanner();
    }

    /**
     * Process types annotated with the provided annotation processors
     * @param annotation the class representing the annotation to scan
     * @param processors the list of processors to process the annotated types
     * @param <T> the type of the annotation
     */
    public static <T extends Annotation> void processAnnotatedTypes(Class<T> annotation, List<AnnotationProcessor<T>> processors) {
        AnnotationScanner scanner = getScanner();

        for (AnnotationProcessor<T> processor : processors)
            scanner.registerAnnotationProcessor(annotation, processor);

        scanner.scan(annotation);
    }

    /**
     * Process types annotated with the provided annotation processor
     * @param annotation the class representing the annotation to scan
     * @param processor the processor to process the annotated types
     * @param <T> the type of the annotation
     */
    public static <T extends Annotation> void processAnnotatedTypes(Class<T> annotation, AnnotationProcessor<T> processor) {
        processAnnotatedTypes(annotation, Collections.singletonList(processor));
    }

    /**
     * Register dependencies annotated with the custom annotation with the similar behaviour as Injectable. Using the
     * consumer, you can perform additional behaviour at registration time.
     * @param annotation the custom annotation
     * @param nameSupplier the supplier to retrieve the dependency name with
     * @param consumer the consumer to do additional processing of the dependencies
     * @param <T> the annotation type
     */
    public static <T extends Annotation> void registerCustomDependencies(Class<T> annotation,
                                                                         CustomInjectableProcessor.NameSupplier<T> nameSupplier, Consumer<Object> consumer) {
        processAnnotatedTypes(annotation, new CustomInjectableProcessor<>(nameSupplier, consumer));
    }
}
