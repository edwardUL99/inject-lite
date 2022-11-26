package io.github.edwardUL99.inject.lite.internal.dependency.registration;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotatedClass;
import io.github.edwardUL99.inject.lite.exceptions.InvalidInjectableException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.InternalAnnotatedClass;
import io.github.edwardUL99.inject.lite.internal.dependency.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class CustomRegistrationStrategyTest {
    @Test
    public void testStrategy() {
        InternalInjector mockInjector = mock(InternalInjector.class);
        Injectable injectable = TestClass.class.getAnnotation(Injectable.class);

        when(mockInjector.canInject(TestClass.class))
                .thenReturn(true);

        DelayedInjectableDependency proxy = new DelayedInjectableDependency("injectable", TestClass.class, mockInjector, true);
        AnnotatedClass<Injectable> annotatedClass = new InternalAnnotatedClass<>(injectable, proxy);
        CustomRegistrationStrategy<Injectable> strategy = new CustomRegistrationStrategy<>(annotatedClass);

        strategy.register(mockInjector);

        verify(mockInjector).canInject(TestClass.class);
        verify(mockInjector).registerInjectableDependency(proxy);
    }

    @Test
    public void testStrategyCannotInject() {
        InternalInjector mockInjector = mock(InternalInjector.class);
        Injectable injectable = TestClass.class.getAnnotation(Injectable.class);

        when(mockInjector.canInject(TestClass.class))
                .thenReturn(false);

        DelayedInjectableDependency proxy = new DelayedInjectableDependency("injectable", TestClass.class, mockInjector, true);
        AnnotatedClass<Injectable> annotatedClass = new InternalAnnotatedClass<>(injectable, proxy);
        CustomRegistrationStrategy<Injectable> strategy = new CustomRegistrationStrategy<>(annotatedClass);

        assertThrows(InvalidInjectableException.class, () -> strategy.register(mockInjector));

        verify(mockInjector).canInject(TestClass.class);
        verify(mockInjector, times(0)).registerInjectableDependency(proxy);
    }

    @Test
    public void testStrategyNotInternalInjector() {
        Injector mockInjector = mock(Injector.class);
        Injectable injectable = TestClass.class.getAnnotation(Injectable.class);

        AnnotatedClass<Injectable> annotatedClass = new InternalAnnotatedClass<>(injectable, null);
        CustomRegistrationStrategy<Injectable> strategy = new CustomRegistrationStrategy<>(annotatedClass);

        assertThrows(IllegalStateException.class, () -> strategy.register(mockInjector));

        verifyNoInteractions(mockInjector);
    }

    @Test
    public void testStrategyNotInternalAnnotatedClass() {
        Injector mockInjector = mock(Injector.class);
        Injectable injectable = TestClass.class.getAnnotation(Injectable.class);

        AnnotatedClass<Injectable> annotatedClass = new InvalidAnnotatedClass<>(injectable, null);

        assertThrows(IllegalStateException.class, () -> new CustomRegistrationStrategy<>(annotatedClass));

        verifyNoInteractions(mockInjector);
    }

    @Injectable("injectable")
    private static class TestClass {}

    private static class InvalidAnnotatedClass<T> extends AnnotatedClass<T> {
        public InvalidAnnotatedClass(T annotation, DelayedInjectableDependency dependency) {
            super(annotation, dependency);
        }
    }
}
