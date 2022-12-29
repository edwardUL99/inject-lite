package io.github.edwardUL99.inject.lite.container;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotatedClass;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationProcessor;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.InternalAnnotatedClass;
import io.github.edwardUL99.inject.lite.internal.dependency.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ContainerAnnotationProcessorTest {
    private final Class<Injectable> annotation = Injectable.class;
    private AnnotationProcessor<Injectable> mockProcessor;
    private ContainerAnnotationProcessor<Injectable> processor;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void init() {
        mockProcessor = (AnnotationProcessor<Injectable>) mock(AnnotationProcessor.class);
        processor = new ContainerAnnotationProcessor<>(annotation, mockProcessor);
    }

    @Test
    public void testProcess() {
        List<AnnotatedClass<Injectable>> annotatedClasses = new ArrayList<>();
        AnnotatedClass<Injectable> annotatedClass = new InternalAnnotatedClass<>(
                TestClass.class.getAnnotation(Injectable.class),
                new DelayedInjectableDependency("test", TestClass.class, mock(InternalInjector.class))
        );
        AnnotatedClass<Injectable> annotatedClass1 = new InternalAnnotatedClass<>(
                TestClass1.class.getAnnotation(Injectable.class),
                new DelayedInjectableDependency("test1", TestClass1.class, mock(InternalInjector.class))
        );
        annotatedClasses.add(annotatedClass);
        annotatedClasses.add(annotatedClass1);

        processor.process(annotatedClasses);

        verify(mockProcessor).process(annotatedClasses);
    }

    @Injectable("test")
    private static class TestClass {}

    @Injectable("test1")
    private static class TestClass1 {}
}