package io.github.edwardUL99.inject.lite.internal.constructors;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Name;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.internal.dependency.Dependency;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencyGraph;
import io.github.edwardUL99.inject.lite.internal.injector.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class DefaultConstructorInjectorTest {
    private InternalInjector<DelayedInjectableDependency> mockInjector;
    private DefaultConstructorInjector constructorInjector;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void init() {
        mockInjector = mock(InternalInjector.class);
        constructorInjector = new DefaultConstructorInjector(mockInjector);

        when(mockInjector.firstMatchSelector())
                .thenCallRealMethod();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInjectorInjectConstructor() {
        when(mockInjector.injectWithGraph("numberDependency", Integer.class))
                .thenReturn(42);
        List<DelayedInjectableDependency> mockDependencies = new ArrayList<>();
        DelayedInjectableDependency mockDependency = mock(DelayedInjectableDependency.class);
        mockDependencies.add(mockDependency);

        when(mockInjector.getInjectableDependencies(String.class))
                .thenReturn(mockDependencies);
        when(mockInjector.injectWithGraph(eq(String.class), eq(mockDependency), any(Function.class)))
                .thenReturn("Hello World");

        TestInjectableClass injectableClass =
                (TestInjectableClass) constructorInjector.injectConstructor("test", TestInjectableClass.class);

        assertEquals(injectableClass.value, "Hello World");
        assertEquals(injectableClass.number, 42);

        verify(mockInjector).injectWithGraph("numberDependency", Integer.class);
        verify(mockInjector).injectWithGraph(eq(String.class), eq(mockDependency), any(Function.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInjectorInjectConstructorWithGraph() {
        DependencyGraph graph = mock(DependencyGraph.class);
        constructorInjector.setDependencyGraph(graph);
        when(mockInjector.injectWithGraph("numberDependency", Integer.class))
                .thenReturn(42);

        List<DelayedInjectableDependency> mockDependencies = new ArrayList<>();
        DelayedInjectableDependency mockDependency = mock(DelayedInjectableDependency.class);
        mockDependencies.add(mockDependency);

        when(mockInjector.getInjectableDependencies(String.class))
                .thenReturn(mockDependencies);
        when(mockInjector.injectWithGraph(eq(String.class), eq(mockDependency), any(Function.class)))
                .thenReturn("Hello World");
        when(mockDependency.getName())
                .thenReturn("stringValue");

        TestInjectableClass injectableClass =
                (TestInjectableClass) constructorInjector.injectConstructor("test", TestInjectableClass.class);

        assertEquals(injectableClass.value, "Hello World");
        assertEquals(injectableClass.number, 42);

        constructorInjector.setDependencyGraph(null);

        verify(mockInjector).injectWithGraph("numberDependency", Integer.class);
        verify(mockInjector).injectWithGraph(eq(String.class), eq(mockDependency), any(Function.class));
        verify(graph).addDependency(new Dependency("test", TestInjectableClass.class),
                new Dependency("stringValue", String.class));
        verify(graph).addDependency(new Dependency("test", TestInjectableClass.class),
                new Dependency("numberDependency", Integer.class));
    }

    @Test
    public void testNoArgConstructor() {
        TestNoArgClass injectableClass =
                (TestNoArgClass) constructorInjector.injectConstructor("test", TestNoArgClass.class);

        assertNotNull(injectableClass);

        verifyNoInteractions(mockInjector);
    }

    @Test
    public void testNoInjectConstructor() {
        assertThrows(InjectionException.class, () ->
                constructorInjector.injectConstructor("test", TestClassNoInjectConstructor.class));

        verifyNoInteractions(mockInjector);
    }

    @Test
    public void testDuplicateInjectConstructor() {
        assertThrows(InjectionException.class, () ->
                constructorInjector.injectConstructor("test", TestClassDuplicateInject.class));

        verifyNoInteractions(mockInjector);
    }

    @Test
    public void testInjectNoArgConstructor() {
        assertThrows(InjectionException.class, () ->
                constructorInjector.injectConstructor("test", TestInjectNoArgs.class));

        verifyNoInteractions(mockInjector);
    }

    public static class TestInjectableClass {
        private final String value;
        private final Integer number;

        @Inject
        public TestInjectableClass(String value, @Name("numberDependency") Integer number) {
            this.value = value;
            this.number = number;
        }
    }

    public static class TestNoArgClass {}

    public static class TestClassNoInjectConstructor {
        public TestClassNoInjectConstructor(String val) {}
    }

    public static class TestClassDuplicateInject {
        @Inject
        public TestClassDuplicateInject(String val) {}

        @Inject
        public TestClassDuplicateInject(String val, Integer number) {

        }
    }

    public static class TestInjectNoArgs {
        @Inject
        public TestInjectNoArgs() {

        }
    }
}
