package io.github.edwardUL99.inject.lite.internal.annotations.processing;

import io.github.edwardUL99.inject.lite.annotations.processing.AnnotatedClass;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationProcessor;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationScanner;
import io.github.edwardUL99.inject.lite.annotations.processing.ProcessOrder;
import io.github.edwardUL99.inject.lite.internal.dependency.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.reflections.Reflections;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default implementation of the default annotation
 */
public class DefaultAnnotationScanner implements AnnotationScanner {
    /**
     * Used for classpath scanner
     */
    private static final Reflections reflections = ReflectionUtils.getReflections();
    /**
     * Injector to inject with
     */
    private final InternalInjector injector;

    /**
     * Instantiate the scanner with the provided constructor and resource injectors
     * @param injector the injector instance
     */
    public DefaultAnnotationScanner(InternalInjector injector) {
        this.injector = injector;
    }

    /**
     * Map of annotation classes to the list of processors
     */
    private final Map<Class<? extends Annotation>, List<AnnotationProcessor<? extends Annotation>>> processors
            = new HashMap<>();

    @Override
    public <T extends Annotation> void registerAnnotationProcessor(Class<T> annotation, AnnotationProcessor<T> processor) {
        List<AnnotationProcessor<? extends Annotation>> processors
                = this.processors.computeIfAbsent(annotation, v -> new ArrayList<>());
        processors.add(processor);
    }

    @Override
    public void scan() {
        for (Class<? extends Annotation> cls : processors.keySet())
            scan(cls);
    }

    private static int processOrderComparator(Class<?> c1, Class<?> c2) {
        ProcessOrder c1Order = c1.getAnnotation(ProcessOrder.class);
        ProcessOrder c2Order = c2.getAnnotation(ProcessOrder.class);

        return ((c1Order != null) ? c1Order.value() : Integer.MAX_VALUE) -
                ((c2Order != null) ? c2Order.value() : Integer.MAX_VALUE);
    }

    protected List<Class<?>> sortClasses(List<Class<?>> classes) {
        classes.sort(DefaultAnnotationScanner::processOrderComparator);

        return classes;
    }

    protected <T extends Annotation> List<Class<?>> scanClasses(Class<T> annotation) {
        return new ArrayList<>(reflections.getTypesAnnotatedWith(annotation));
    }

    private <T extends Annotation> List<AnnotatedClass<T>> getAnnotatedClasses(Class<T> annotation) {
        return sortClasses(scanClasses(annotation)).stream()
            .map(cls -> {
                DelayedInjectableDependency proxy = new DelayedInjectableDependency(cls.getSimpleName(), cls, injector);

                return new InternalAnnotatedClass<>(cls.getAnnotation(annotation), proxy);
            })
            .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Annotation> void scan(Class<T> annotation) {
        List<AnnotatedClass<T>> annotatedClasses = getAnnotatedClasses(annotation);
        List<AnnotationProcessor<? extends Annotation>> processors
                = this.processors.getOrDefault(annotation, new ArrayList<>());

        if (annotatedClasses.size() > 0)
            for (AnnotationProcessor<?> processor : processors)
                ((AnnotationProcessor<T>)processor).process(annotatedClasses);
    }
}
