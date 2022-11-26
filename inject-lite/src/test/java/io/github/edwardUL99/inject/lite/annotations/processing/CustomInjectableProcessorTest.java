package io.github.edwardUL99.inject.lite.annotations.processing;

import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.InternalAnnotatedClass;
import io.github.edwardUL99.inject.lite.internal.container.ContainersInternal;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategies;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategy;
import io.github.edwardUL99.inject.lite.internal.dependency.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;


public class CustomInjectableProcessorTest {
    private InternalInjector mockInjector;
    private CustomInjectableProcessor<TestAnnotation> processor;

    @BeforeEach
    public void init() {
        mockInjector = mock(InternalInjector.class);
        processor = new CustomInjectableProcessor<>(mockInjector, a -> a.getAnnotation().value());
    }

    private static List<AnnotatedClass<TestAnnotation>> mockList(AnnotatedClass<TestAnnotation> annotatedClass) {
        List<AnnotatedClass<TestAnnotation>> list = new ArrayList<>();
        list.add(annotatedClass);

        return list;
    }

    @Test
    public void testProcess() {
        try (MockedStatic<RegistrationStrategies> strategies = mockStatic(RegistrationStrategies.class);
             MockedStatic<ContainersInternal> containers = mockStatic(ContainersInternal.class)) {
            // won't actually be injecting so use null values
            DelayedInjectableDependency proxy = new DelayedInjectableDependency("test", TestClass.class, null);
            AnnotatedClass<TestAnnotation> annotatedClass = new InternalAnnotatedClass<>(TestClass.class.getAnnotation(TestAnnotation.class),
                    proxy);

            RegistrationStrategy mockStrategy = mock(RegistrationStrategy.class);
            strategies.when(() -> RegistrationStrategies.forCustom(annotatedClass))
                            .thenReturn(mockStrategy);
            containers.when(() -> ContainersInternal.registerDependencyCheckingContainer(mockInjector,
                    mockStrategy, TestClass.class))
                            .thenReturn(true);

            verifyNoInteractions(mockInjector);

            processor.process(mockList(annotatedClass));

            strategies.verify(() -> RegistrationStrategies.forCustom(annotatedClass));
            containers.verify(() -> ContainersInternal.registerDependencyCheckingContainer(mockInjector,
                    mockStrategy, TestClass.class));
            verify(mockInjector).inject("name", TestClass.class);
        }
    }

    @Test
    public void testProcessNotInjected() {
        try (MockedStatic<RegistrationStrategies> strategies = mockStatic(RegistrationStrategies.class);
             MockedStatic<ContainersInternal> containers = mockStatic(ContainersInternal.class)) {
            // won't actually be injecting so use null values
            DelayedInjectableDependency proxy = new DelayedInjectableDependency("test", TestClass.class, null);
            AnnotatedClass<TestAnnotation> annotatedClass = new InternalAnnotatedClass<>(TestClass.class.getAnnotation(TestAnnotation.class),
                    proxy);

            RegistrationStrategy mockStrategy = mock(RegistrationStrategy.class);
            strategies.when(() -> RegistrationStrategies.forCustom(annotatedClass))
                    .thenReturn(mockStrategy);
            containers.when(() -> ContainersInternal.registerDependencyCheckingContainer(mockInjector,
                            mockStrategy, TestClass.class))
                    .thenReturn(false);

            verifyNoInteractions(mockInjector);

            processor.process(mockList(annotatedClass));

            strategies.verify(() -> RegistrationStrategies.forCustom(annotatedClass));
            containers.verify(() -> ContainersInternal.registerDependencyCheckingContainer(mockInjector,
                    mockStrategy, TestClass.class));
            verifyNoInteractions(mockInjector);
        }
    }

    @Test
    public void testProcessNullName() {
        processor = new CustomInjectableProcessor<>(mockInjector, a -> null);
        DelayedInjectableDependency proxy = new DelayedInjectableDependency("name", TestClass.class, null);
        AnnotatedClass<TestAnnotation> annotatedClass = new InternalAnnotatedClass<>(TestClass.class.getAnnotation(TestAnnotation.class),
                proxy);

        assertThrows(InjectionException.class, () ->
                processor.process(mockList(annotatedClass)));

        verifyNoInteractions(mockInjector);
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
        String value();
    }


    @TestAnnotation("name")
    public static class TestClass {}
}
