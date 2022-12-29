package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.annotations.Lazy;
import io.github.edwardUL99.inject.lite.annotations.Name;
import io.github.edwardUL99.inject.lite.annotations.Optional;
import io.github.edwardUL99.inject.lite.exceptions.AmbiguousDependencyException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraph;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.proxy.InjectionMethod;
import io.github.edwardUL99.inject.lite.internal.proxy.Proxies;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class CommonDependencyHandlerTest {
    private InternalInjector mockInjector;
    private DependencyGraph mockGraph;
    private CommonDependencyHandler handler;

    @BeforeEach
    public void init() {
        mockInjector = mock(InternalInjector.class);
        mockGraph = mock(DependencyGraph.class);
        handler = new CommonDependencyHandler(mockInjector);
    }

    @AfterEach
    public void teardown() {
        Configuration.global.setLazyDependenciesEnabled(true);
    }

    @Test
    public void testWithOneDependency() {
        List<InjectableDependency> dependencies = new ArrayList<>();
        DelayedInjectableDependency mockDependency = mock(DelayedInjectableDependency.class);
        dependencies.add(mockDependency);

        when(mockInjector.getInjectableDependencies(String.class))
                .thenReturn(dependencies);

        InjectableDependency dependency = handler.getUnnamedDependency(String.class);

        assertEquals(dependency, mockDependency);
        verify(mockInjector).getInjectableDependencies(String.class);
    }

    @Test
    public void testWithMultipleDependency() {
        List<InjectableDependency> dependencies = new ArrayList<>();
        dependencies.add(mock(DelayedInjectableDependency.class));
        dependencies.add(mock(DelayedInjectableDependency.class));

        when(mockInjector.getInjectableDependencies(String.class))
                .thenReturn(dependencies);

        assertThrows(AmbiguousDependencyException.class, () ->
                handler.getUnnamedDependency(String.class));

        verify(mockInjector).getInjectableDependencies(String.class);
    }

    @Test
    public void testGetInjectableDependency() {
        try {
            Configuration.global.setUseParameterNameIfUnnamed(false);
            InjectableDependency mockDependency = mock(InjectableDependency.class);

            when(mockInjector.getInjectableDependency(String.class, CommonDependencyHandler.NULL_SUPPLIER, false))
                    .thenReturn(mockDependency);

            assertEquals(mockDependency,
                    handler.getInjectableDependency(String.class, () -> "test"));
            verify(mockInjector).getInjectableDependency(String.class, CommonDependencyHandler.NULL_SUPPLIER, false);

            Configuration.global.setUseParameterNameIfUnnamed(true);

            reset(mockInjector);
            Supplier<String> nameSupplier = () -> "test";
            when(mockInjector.getInjectableDependency(String.class, nameSupplier, false))
                    .thenReturn(mockDependency);

            assertEquals(mockDependency,
                    handler.getInjectableDependency(String.class, nameSupplier));
            verify(mockInjector).getInjectableDependency(String.class, nameSupplier, false);

            reset(mockInjector);
            when(mockInjector.getInjectableDependency(String.class, nameSupplier, false))
                    .thenReturn(null);

            assertThrows(DependencyNotFoundException.class, () ->
                    handler.getInjectableDependency(String.class, nameSupplier));
            verify(mockInjector).getInjectableDependency(String.class, nameSupplier, false);
        } finally {
            Configuration.global.setUseParameterNameIfUnnamed(false);
        }
    }

    @Test
    public void testInstantiateParameters() throws ReflectiveOperationException {
        CommonDependencyHandler spy = Mockito.spy(handler);
        Parameter[] parameters = TestDependency.class.getDeclaredMethod("method", String.class, Integer.class, Long.class)
                .getParameters();

        DelayedInjectableDependency dependency = new DelayedInjectableDependency("s1", String.class, mockInjector);
        DelayedInjectableDependency dependency1 = new DelayedInjectableDependency("l3", Long.class, mockInjector);
        when(spy.getInjectableDependency(eq(String.class), any()))
                .thenReturn(dependency);
        when(spy.getInjectableDependency(eq(Long.class), any()))
                .thenReturn(dependency1);

        when(mockInjector.injectWithGraph(String.class, dependency))
                .thenReturn("Hello");
        when(mockInjector.injectWithGraph("name", Integer.class))
                .thenReturn(1);
        when(mockInjector.injectWithGraph(Long.class, dependency1))
                .thenReturn(1L);

        Object[] instances = spy.instantiateParameters("name", TestDependency.class, mockGraph, parameters);

        assertArrayEquals(instances, new Object[]{"Hello", 1, 1L});
        verify(mockInjector).injectWithGraph(String.class, dependency);
        verify(mockInjector).injectWithGraph("name", Integer.class);
        verify(mockInjector).injectWithGraph(Long.class, dependency1);
        verify(spy, times(3)).getDependencyCheckingLazy(any(), any(), any());
    }

    @Test
    public void testInstantiateParametersOptional() throws ReflectiveOperationException {
        CommonDependencyHandler spy = Mockito.spy(handler);
        Parameter[] parameters = TestDependency.class.getDeclaredMethod("method", String.class, Integer.class, Long.class)
                .getParameters();

        DelayedInjectableDependency dependency = new DelayedInjectableDependency("s1", String.class, mockInjector);
        DelayedInjectableDependency dependency1 = new DelayedInjectableDependency("l3", Long.class, mockInjector);
        when(spy.getInjectableDependency(eq(String.class), any()))
                .thenReturn(dependency);

        when(mockInjector.injectWithGraph(String.class, dependency))
                .thenReturn("Hello");
        when(mockInjector.injectWithGraph("name", Integer.class))
                .thenReturn(1);
        doThrow(DependencyNotFoundException.class).when(mockInjector).injectWithGraph(Long.class, dependency1);

        Object[] instances = spy.instantiateParameters("name", TestDependency.class, mockGraph, parameters);

        assertArrayEquals(instances, new Object[]{"Hello", 1, null});
        verify(mockInjector).injectWithGraph(String.class, dependency);
        verify(mockInjector).injectWithGraph("name", Integer.class);
        verify(spy, times(3)).getDependencyCheckingLazy(any(), any(), any());
    }

    @Test
    public void testGetDependencyCheckingLazy() throws Exception {
        Lazy lazyParam = TestDependency.class.getDeclaredMethod("method1", String.class, String.class)
            .getParameters()[0].getAnnotation(Lazy.class);
        InjectionMethod mockMethod = mock(InjectionMethod.class);

        try (MockedStatic<Proxies> proxiesMock = mockStatic(Proxies.class);
             MockedStatic<InjectionContext> contextMock = mockStatic(InjectionContext.class)) {
            proxiesMock.when(() -> Proxies.createInjectionProxy(String.class, mockMethod))
                .thenReturn("Hello");

            Object proxied = handler.getDependencyCheckingLazy(lazyParam, String.class, mockMethod);

            assertEquals(proxied, "Hello");
            proxiesMock.verify(() -> Proxies.createInjectionProxy(String.class, mockMethod));

            verifyNoInteractions(mockMethod);
            proxiesMock.reset();

            when(mockMethod.inject())
                .thenReturn("boo");

            proxied = handler.getDependencyCheckingLazy(null, String.class, mockMethod);

            assertEquals(proxied, "boo");
            proxiesMock.verifyNoInteractions();
            verify(mockMethod).inject();

            reset(mockMethod);

            Configuration.global.setLazyDependenciesEnabled(false);
            when(mockMethod.inject())
                    .thenReturn("boo");
            proxied = handler.getDependencyCheckingLazy(lazyParam, String.class, mockMethod);

            assertEquals(proxied, "boo");
            verify(mockMethod).inject();

            reset(mockMethod);
            contextMock.when(InjectionContext::isLazyBehaviourDisabled)
                        .thenReturn(true);
            when(mockMethod.inject())
                    .thenReturn("boo");
            proxied = handler.getDependencyCheckingLazy(lazyParam, String.class, mockMethod);

            assertEquals(proxied, "boo");
            verify(mockMethod).inject();
        }
    }

    private static class TestDependency {
        public static void method(String s1, @Name("name") Integer i2, @Optional Long l3) {}

        public static void method1(@Lazy String s, String s1) {}
    }
}