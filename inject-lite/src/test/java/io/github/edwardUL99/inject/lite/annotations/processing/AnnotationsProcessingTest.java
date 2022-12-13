package io.github.edwardUL99.inject.lite.annotations.processing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

public class AnnotationsProcessingTest {
    private AnnotationScanner mockScanner;

    @BeforeEach
    public void init() {
        mockScanner = mock(AnnotationScanner.class);
    }

    private MockedStatic<AnnotationScanners> mockScanners() {
        MockedStatic<AnnotationScanners> scanners = mockStatic(AnnotationScanners.class);
        scanners.when(AnnotationScanners::newScanner)
                .thenReturn(mockScanner);

        return scanners;
    }

    @Test
    public void testProcessAnnotatedTypesList() {
        try (MockedStatic<AnnotationScanners> ignored = mockScanners()) {
            StubProcessor<TestAnnotation> stubProcessor = new StubProcessor<>();
            StubProcessor<TestAnnotation> stubProcessor1 = new StubProcessor<>();
            List<AnnotationProcessor<TestAnnotation>> processors = new ArrayList<>();
            processors.add(stubProcessor);
            processors.add(stubProcessor1);

            AnnotationsProcessing.processAnnotatedTypes(TestAnnotation.class, processors);

            verify(mockScanner).registerAnnotationProcessor(TestAnnotation.class, stubProcessor);
            verify(mockScanner).registerAnnotationProcessor(TestAnnotation.class, stubProcessor1);
            verify(mockScanner).scan(TestAnnotation.class);
        }
    }

    @Test
    public void testProcessAnnotatedTypesSingle() {
        try (MockedStatic<AnnotationScanners> ignored = mockScanners()) {
            StubProcessor<TestAnnotation> stubProcessor = new StubProcessor<>();

            AnnotationsProcessing.processAnnotatedTypes(TestAnnotation.class, stubProcessor);

            verify(mockScanner).registerAnnotationProcessor(TestAnnotation.class, stubProcessor);
            verify(mockScanner).scan(TestAnnotation.class);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterCustomDependencies() {
        try (MockedStatic<AnnotationScanners> ignored = mockScanners()) {
            CustomInjectableProcessor.NameSupplier<TestAnnotation> nameSupplier = a -> "name";
            Consumer<Object> consumer = o -> {};

            AnnotationsProcessing.registerCustomDependencies(TestAnnotation.class, nameSupplier, consumer);

            verify(mockScanner).registerAnnotationProcessor(eq(TestAnnotation.class), any(CustomInjectableProcessor.class));
            verify(mockScanner).scan(TestAnnotation.class);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    private @interface TestAnnotation {
    }

    private static class StubProcessor<T extends Annotation> implements AnnotationProcessor<T> {
        @Override
        public void process(List<AnnotatedClass<T>> annotatedClasses) {
        }
    }
}