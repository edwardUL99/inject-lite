package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DelayedInjectableDependencyTest {
    private FieldInjector fieldInjector;
    private ConstructorInjector constructorInjector;
    private InternalInjector<DelayedInjectableDependency> injector;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void init() {
        injector = mock(InternalInjector.class);
        fieldInjector = mock(FieldInjector.class);
        constructorInjector = mock(ConstructorInjector.class);

        when(injector.getFieldInjector())
                .thenReturn(fieldInjector);
        when(injector.getConstructorInjector())
                .thenReturn(constructorInjector);
    }

    @Test
    public void testInjectOnGetInjectable() {
        DelayedInjectableDependency dependency = new DelayedInjectableDependency("test", TestDependency.class, injector);
        // test the static tracking of instances
        DelayedInjectableDependency dependency1 = new DelayedInjectableDependency("test", TestDependency.class, injector);
        // test that different name but same class is a different dependency
        DelayedInjectableDependency dependency2 = new DelayedInjectableDependency("test1", TestDependency.class, injector);

        when(constructorInjector.injectConstructor("test", TestDependency.class))
                .thenReturn(new TestDependency());
        when(constructorInjector.injectConstructor("test1", TestDependency.class))
                .thenReturn(new TestDependency());

        Object testDependency = dependency.get();
        Object testDependency1 = dependency.get();
        Object testDependency2 = dependency1.get();
        Object testDependency3 = dependency2.get();

        assertInstanceOf(TestDependency.class, testDependency);
        assertSame(testDependency, testDependency1);
        assertSame(testDependency1, testDependency2);
        assertNotSame(testDependency, testDependency3);
        assertNotSame(testDependency1, testDependency3);
        assertNotSame(testDependency2, testDependency3);
        verify(fieldInjector, times(1)).injectFields(testDependency);
        verify(fieldInjector, times(1)).injectFields(testDependency3);
        verify(constructorInjector, times(1)).injectConstructor("test", TestDependency.class);
        verify(constructorInjector, times(1)).injectConstructor("test1", TestDependency.class);
    }

    @Test
    public void testInjectOnGetInjectableSingleton() {
        try {
            InjectionContext.setSingletonBehaviour(false);
            DelayedInjectableDependency dependency = new DelayedInjectableDependency("test", TestDependency.class, injector);

            when(constructorInjector.injectConstructor("test", TestDependency.class))
                    .thenReturn(new TestDependency());

            Object testDependency = dependency.get();

            when(constructorInjector.injectConstructor("test", TestDependency.class))
                    .thenReturn(new TestDependency());

            Object testDependency1 = dependency.get();

            assertInstanceOf(TestDependency.class, testDependency);
            assertNotSame(testDependency, testDependency1);
            verify(fieldInjector, times(1)).injectFields(testDependency);
            verify(fieldInjector, times(1)).injectFields(testDependency1);
            verify(constructorInjector, times(2)).injectConstructor("test", TestDependency.class);
        } finally {
            InjectionContext.setSingletonBehaviour(true);
        }
    }

    public static class TestDependency {}
}
