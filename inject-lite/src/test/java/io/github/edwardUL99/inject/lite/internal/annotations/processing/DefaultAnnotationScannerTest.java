package io.github.edwardUL99.inject.lite.internal.annotations.processing;

import io.github.edwardUL99.inject.lite.annotations.processing.AnnotatedClass;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationProcessor;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.reflections.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.edwardUL99.inject.lite.utils.TestUtils.setInternalStaticField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.getInternalState;

public class DefaultAnnotationScannerTest {
    private DefaultAnnotationScanner scanner;
    private Reflections reflectionsMock;
    private InternalInjector mockInjector;
    private Map<Class<? extends Annotation>, List<AnnotationProcessor<? extends Annotation>>> processors;

    public static Set<Class<?>> mockSet() {
        Set<Class<?>> set = new LinkedHashSet<>();
        set.add(TestClass.class);

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
        setInternalStaticField(DefaultAnnotationScanner.class, "reflections", reflectionsMock);
        processors = getInternalState(scanner, "processors");
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
                .thenReturn(mockSet());
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
                .thenReturn(mockSet());

        scanner.scan(TestAnnotation.class);

        assertTrue(processor.called);
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

    public static class TestProcessor<T extends Annotation> implements AnnotationProcessor<T> {
        private boolean called;

        @Override
        public void process(List<AnnotatedClass<T>> classes) {
            called = true;
        }
    }
}
