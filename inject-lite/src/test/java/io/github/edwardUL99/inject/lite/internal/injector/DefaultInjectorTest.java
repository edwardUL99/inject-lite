package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.annotations.Priority;
import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.exceptions.DependencyExistsException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyMismatchException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.exceptions.InvalidInjectableException;
import io.github.edwardUL99.inject.lite.internal.dependency.CommonDependencyHandler;
import io.github.edwardUL99.inject.lite.internal.dependency.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.utils.ThreadAwareValue;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultInjectorTest {
    private Map<String, InjectableDependency> injectables;
    private DefaultInjector injector;
    private FieldInjector mockFieldInjector;
    private ConstructorInjector mockConstructorInjector;
    private CommonDependencyHandler mockHandler;

    @BeforeEach
    public void init() {
        DelayedInjectableDependency.setInstances(new ThreadAwareValue<>(new HashMap<>(), true));
        injector = Mockito.spy(new DefaultInjector(new DelayedInjectableDependency.Factory()));
        injectables = injector.injectables;

        mockFieldInjector = mock(FieldInjector.class);
        mockConstructorInjector = mock(ConstructorInjector.class);

        mockHandler = mock(CommonDependencyHandler.class);
        when(injector.getDependencyHandler())
            .thenReturn(mockHandler);

        when(injector.getFieldInjector())
                .thenReturn(mockFieldInjector);
        when(injector.getConstructorInjector())
                .thenReturn(mockConstructorInjector);
    }

    @Test
    public void testRegisterDependency() {
        injector.registerDependency("dependency", TestDependency.class, true);

        assertEquals(injectables.size(), 1);
        InjectableDependency proxy = injectables.get("dependency");
        assertEquals(proxy.getType(), TestDependency.class);

        // should throw an exception
        assertThrows(DependencyExistsException.class, () ->
                injector.registerDependency("dependency", TestDependency.class, true));
    }

    @Test
    public void testRegisterConstantDependency() {
        injector.registerConstantDependency("name", long.class, 1L);

        assertEquals(injectables.size(), 1);
        InjectableDependency dependency = injectables.get("name");
        assertEquals(1L, dependency.get());

        // should throw an exception
        assertThrows(DependencyExistsException.class, () ->
                injector.registerConstantDependency("name", long.class, 1L));
    }

    @Test
    public void testRegisterInvalidDependency() {
        Class<?>[] invalid = {
                TestAbstract.class,
                Enum.class,
                TestInterface.class
        };

        for (Class<?> cls : invalid)
            assertThrows(InvalidInjectableException.class, () ->
                    injector.registerDependency("name", cls, true));
    }

    private interface TestInterface {}

    private static abstract class TestAbstract {}

    @Test
    public void testInjectByName() {
        when(mockConstructorInjector.injectConstructor("dependency", TestDependency.class))
                .thenReturn(new TestSubclass());
        injector.registerDependency("dependency", TestDependency.class, true);
        TestDependency testDependency = injector.inject("dependency", TestDependency.class);
        assertNotNull(testDependency);

        // should throw now found
        assertThrows(DependencyNotFoundException.class, () ->
                injector.inject("dependency1", TestDependency.class));

        // should throw mismatch
        assertThrows(DependencyMismatchException.class, () ->
                injector.inject("dependency", String.class));
        verify(mockConstructorInjector).injectConstructor("dependency", TestDependency.class);
        verify(mockFieldInjector).injectFields(testDependency);
    }

    @Test
    public void testInjectByType() {
        when(mockConstructorInjector.injectConstructor("dependency", TestSubclass.class))
                .thenReturn(new TestSubclass());
        injector.registerDependency("dependency", TestSubclass.class, true);
        TestDependency testDependency = injector.inject(TestDependency.class);
        assertInstanceOf(TestSubclass.class, testDependency);

        // should throw not found
        assertThrows(DependencyNotFoundException.class, () ->
                injector.inject(String.class));
        verify(mockConstructorInjector).injectConstructor("dependency", TestSubclass.class);
        verify(mockFieldInjector).injectFields(testDependency);
    }

    private void setUpMockInjectables() {
        injectables = new HashMap<>();
        injectables.put("test", new DelayedInjectableDependency("test", TestDependency.class, injector));
        injectables.put("test1", new DelayedInjectableDependency("test1", TestSubclass.class, injector));
        injectables.put("test2", new DelayedInjectableDependency("test2", TestDependency1.class, injector));
        injector.setInjectablesForTest(injectables);

        when(mockConstructorInjector.injectConstructor("test", TestDependency.class))
                .thenReturn(new TestDependency());
        when(mockConstructorInjector.injectConstructor("test1", TestSubclass.class))
                .thenReturn(new TestSubclass());
        when(mockConstructorInjector.injectConstructor("test2", TestDependency1.class))
                .thenReturn(new TestDependency1());
    }

    @Test
    public void testActOnDependenciesByType() {
        setUpMockInjectables();
        List<TestDependency> testDependencyList = new ArrayList<>();
        List<TestDependency1> testDependency1List = new ArrayList<>();
        Consumer<TestDependency> testDependencyConsumer = testDependencyList::add;
        Consumer<TestDependency1> testDependency1Consumer = testDependency1List::add;

        injector.actOnDependencies(testDependencyConsumer, TestDependency.class);
        injector.actOnDependencies(testDependency1Consumer, TestDependency1.class);

        assertEquals(2, testDependencyList.size());
        assertEquals(1, testDependency1List.size());

        verify(mockConstructorInjector, times(3)).injectConstructor(any(), any());
        verify(mockFieldInjector, times(3)).injectFields(any());;
    }

    @Test
    public void testActOnDependencies() {
        setUpMockInjectables();
        List<Object> dependencyList = new ArrayList<>();
        Consumer<Object> testDependencyConsumer = dependencyList::add;

        injector.actOnDependencies(testDependencyConsumer);

        assertEquals(3, dependencyList.size());

        verify(mockConstructorInjector, times(3)).injectConstructor(any(), any());
        verify(mockFieldInjector, times(3)).injectFields(any());;
    }

    @Test
    public void testInstantiate() {
        try (MockedStatic<InjectionContext> mockInjectionContext = mockStatic(InjectionContext.class)) {
            TestDependency dependency = new TestDependency();

            when(mockConstructorInjector.injectConstructor("TestDependency", TestDependency.class))
                    .thenReturn(dependency);
            doNothing().when(mockFieldInjector).injectFields(dependency);

            TestDependency returned = injector.instantiate(TestDependency.class);

            assertSame(returned, dependency);
            verify(mockConstructorInjector).injectConstructor("TestDependency", TestDependency.class);
            verify(mockFieldInjector).injectFields(dependency);

            mockInjectionContext.verify(() -> InjectionContext.setSingletonBehaviour(false));
            mockInjectionContext.verify(() -> InjectionContext.setSingletonBehaviour(true));
            mockInjectionContext.verify(() -> InjectionContext.setLazyBehaviourDisabled(true));
            mockInjectionContext.verify(() -> InjectionContext.setLazyBehaviourDisabled(false));
        }
    }

    @Test
    public void testSharedInjectionExecutor() {
        try (MockedStatic<Injection> mockInjection = mockStatic(Injection.class)) {
            AsynchronousExecutor mockExecutor = mock(AsynchronousExecutor.class);
            mockInjection.when(Injection::sharedInjectionExecutor)
                    .thenReturn(mockExecutor);

            AsynchronousExecutor executor = injector.sharedInjectionExecutor();
            assertEquals(mockExecutor, executor);

            mockInjection.verify(Injection::sharedInjectionExecutor);
        }
    }

    @Test
    public void testInvalidInjectable() {
        assertThrows(InvalidInjectableException.class, () ->
                injector.registerDependency("cannotInject", CannotInject.class, true));
    }

    @Test
    public void testInjectAll() {
        setUpMockInjectables();

        Map<String, TestDependency> dependencies = injector.injectAll(TestDependency.class);

        assertEquals(2, dependencies.size());
        verify(mockConstructorInjector).injectConstructor("test", TestDependency.class);
        verify(mockConstructorInjector).injectConstructor("test1", TestSubclass.class);
        verify(mockFieldInjector).injectFields(injectables.get("test").get());
        verify(mockFieldInjector).injectFields(injectables.get("test1").get());
    }

    @Test
    public void testGetInjectableDependencies() {
        setUpMockInjectables();

        List<InjectableDependency> dependencies = injector.getInjectableDependencies(TestDependency.class);

        assertEquals(2, dependencies.size());
        assertEquals(TestDependency.class, dependencies.get(0).getType());
        assertEquals(TestSubclass.class, dependencies.get(1).getType());
    }

    @Test
    public void testGetInjectableDependency() {
        setUpMockInjectables();

        Configuration.global.setSelectFirstDependency(true);
        InjectableDependency dependency = injector.getInjectableDependency(TestDependency.class);
        assertEquals(TestDependency.class, dependency.getType());

        Configuration.global.setSelectFirstDependency(false);
        dependency = injector.getInjectableDependency(TestDependency.class);
        assertEquals(TestSubclass.class, dependency.getType());
    }

    @Test
    public void testGetInjectableDependencyWithAmbiguousCheck() {
        Configuration.global.setRequireNamedMultipleMatch(true);

        DelayedInjectableDependency mockDependency = mock(DelayedInjectableDependency.class);

        when(mockHandler.getUnnamedDependency(TestDependency.class))
            .thenReturn(mockDependency);

        InjectableDependency dependency = injector.getInjectableDependency(TestDependency.class);
        assertEquals(dependency, mockDependency);

        verify(mockHandler).getUnnamedDependency(TestDependency.class);

        Configuration.global.setRequireNamedMultipleMatch(false);
    }

    @Priority(2)
    public static class TestDependency {}

    @Priority(1)
    public static class TestSubclass extends TestDependency {}

    public static class TestDependency1 {}

    static class CannotInject {}
}
