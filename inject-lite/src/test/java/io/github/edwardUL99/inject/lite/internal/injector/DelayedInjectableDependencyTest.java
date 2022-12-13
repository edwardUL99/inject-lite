package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.dependency.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.methods.MethodInjector;
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
    private MethodInjector methodInjector;
    private InternalInjector injector;

    @BeforeEach
    public void init() {
        injector = mock(InternalInjector.class);
        fieldInjector = mock(FieldInjector.class);
        constructorInjector = mock(ConstructorInjector.class);
        methodInjector = mock(MethodInjector.class);

        when(injector.getFieldInjector())
                .thenReturn(fieldInjector);
        when(injector.getConstructorInjector())
                .thenReturn(constructorInjector);
        when(injector.getMethodInjector())
                .thenReturn(methodInjector);
    }

    @Test
    public void testInjectOnGetInjectable() {
        DelayedInjectableDependency dependency = new DelayedInjectableDependency("test", TestDependency.class, injector);
        // test the static tracking of instances
        DelayedInjectableDependency dependency1 = new DelayedInjectableDependency("test", TestDependency.class, injector);
        // test that different name but same class is a different dependency
        DelayedInjectableDependency dependency2 = new DelayedInjectableDependency("test1", TestDependency.class, injector);

        TestDependency test = new TestDependency();
        TestDependency test1 = new TestDependency();

        when(constructorInjector.injectConstructor("test", TestDependency.class))
                .thenReturn(test);
        when(constructorInjector.injectConstructor("test1", TestDependency.class))
                .thenReturn(test1);

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
        verify(fieldInjector).injectFields(testDependency);
        verify(fieldInjector).injectFields(testDependency3);
        verify(constructorInjector).injectConstructor("test", TestDependency.class);
        verify(constructorInjector).injectConstructor("test1", TestDependency.class);
        verify(methodInjector).injectMethods("test", test);
        verify(methodInjector).injectMethods("test1", test1);
    }

    @Test
    public void testInjectOnGetInjectableSingletonDisabled() {
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
            verify(fieldInjector).injectFields(testDependency);
            verify(fieldInjector).injectFields(testDependency1);
            verify(constructorInjector, times(2)).injectConstructor("test", TestDependency.class);
            verify(methodInjector).injectMethods("test", testDependency);
            verify(methodInjector).injectMethods("test", testDependency1);
        } finally {
            InjectionContext.setSingletonBehaviour(true);
        }
    }

    public static class TestDependency {}
}
