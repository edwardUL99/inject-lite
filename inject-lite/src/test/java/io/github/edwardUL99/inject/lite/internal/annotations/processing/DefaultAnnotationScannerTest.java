package io.github.edwardUL99.inject.lite.internal.annotations.processing;

import io.github.edwardUL99.inject.lite.annotations.processing.AnnotatedClass;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationProcessor;
import io.github.edwardUL99.inject.lite.annotations.processing.ProcessOrder;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.reflections.Reflections;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultAnnotationScannerTest {
    private DefaultAnnotationScanner scanner;
    private Reflections reflectionsMock;
    private InternalInjector mockInjector;
    private Map<Class<? extends Annotation>, List<AnnotationProcessor<? extends Annotation>>> processors;

    public static Set<Class<?>> mockSet(boolean addSort) {
        Set<Class<?>> set = new LinkedHashSet<>();
        set.add(TestClass.class);
        if (addSort) set.add(TestClassSort.class);

        return set;
    }

    @BeforeEach
    public void init() {
        FieldInjector mockFieldInjector = mock(FieldInjector.class);
        ConstructorInjector mockConstructorInjector = mock(ConstructorInjector.class);
        mockInjector = mock(InternalInjector.class);

        when(mockInjector.getFieldInjector())
                .thenReturn(mockFieldInjector);
        when(mockInjector.getConstructorInjector())
                .thenReturn(mockConstructorInjector);

        scanner = new DefaultAnnotationScanner(mockInjector);
        reflectionsMock = mock(Reflections.class);
        DefaultAnnotationScanner.setReflections(reflectionsMock);
        processors = scanner.getProcessors();
    }

    @AfterAll
    public static void after() {
        DefaultAnnotationScanner.setReflections(ReflectionUtils.getReflections());
    }

    @Test
    public void testRegisterAnnotationProcessor() {
        AnnotationProcessor<TestAnnotation> processor = System.out::println;
        scanner.registerAnnotationProcessor(TestAnnotation.class, processor);

        assertEquals(1, processors.size());
        assertEquals(processors.get(TestAnnotation.class).get(0), processor);
    }

    @Test
    public void testScan() {
        TestProcessor<TestAnnotation> processor = new TestProcessor<>();
        scanner.registerAnnotationProcessor(TestAnnotation.class, processor);
        TestProcessor<TestAnnotation1> processor1 = new TestProcessor<>();
        scanner.registerAnnotationProcessor(TestAnnotation1.class, processor1);

        when(reflectionsMock.getTypesAnnotatedWith(TestAnnotation.class))
                .thenReturn(mockSet(false));
        when(reflectionsMock.getTypesAnnotatedWith(TestAnnotation1.class))
                .thenReturn(Collections.emptySet());

        scanner.scan();

        assertTrue(processor.called);
        assertFalse(processor1.called);
        verify(reflectionsMock).getTypesAnnotatedWith(TestAnnotation.class);
        verify(reflectionsMock).getTypesAnnotatedWith(TestAnnotation1.class);
    }

    @Test
    public void testScanWithTypeSpecified() {
        TestProcessor<TestAnnotation> processor = new TestProcessor<>();
        scanner.registerAnnotationProcessor(TestAnnotation.class, processor);

        when(reflectionsMock.getTypesAnnotatedWith(TestAnnotation.class))
                .thenReturn(mockSet(false));

        scanner.scan(TestAnnotation.class);

        assertTrue(processor.called);
        verify(reflectionsMock).getTypesAnnotatedWith(TestAnnotation.class);
    }

    @Test
    public void testScanWithSort() {
        TestProcessor<TestAnnotation> processor = new TestProcessor<>();
        Set<Class<?>> classes = mockSet(true);
        List<Class<?>> sorted = Arrays.asList(TestClassSort.class, TestClass.class);
        scanner.registerAnnotationProcessor(TestAnnotation.class, processor);

        when(reflectionsMock.getTypesAnnotatedWith(TestAnnotation.class))
                .thenReturn(classes);

        scanner.scan();

        assertTrue(processor.called);
        assertEquals(sorted, processor.calledClasses);
        verify(reflectionsMock).getTypesAnnotatedWith(TestAnnotation.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface TestAnnotation {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface TestAnnotation1 {
        String value();
    }

    @TestAnnotation("test")
    public static class TestClass {

    }

    @TestAnnotation("test")
    @ProcessOrder(1)
    public static class TestClassSort {

    }

    public static class TestProcessor<T extends Annotation> implements AnnotationProcessor<T> {
        private boolean called;
        private List<Class<?>> calledClasses;

        @Override
        public void process(List<AnnotatedClass<T>> classes) {
            called = true;
            calledClasses = classes.stream()
                    .map(AnnotatedClass::getAnnotatedClass)
                    .collect(Collectors.toList());;
        }
    }
}
