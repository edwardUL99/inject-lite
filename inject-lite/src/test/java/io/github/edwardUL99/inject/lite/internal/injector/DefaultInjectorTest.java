package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.exceptions.DependencyExistsException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyMismatchException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.exceptions.InvalidInjectableException;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
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

import static io.github.edwardUL99.inject.lite.utils.TestUtils.setInternalStaticField;
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
import static org.powermock.reflect.Whitebox.setInternalState;

public class DefaultInjectorTest {
    private Map<String, DelayedInjectableDependency> injectables;
    private DefaultInjector<DelayedInjectableDependency> injector;
    private FieldInjector mockFieldInjector;
    private ConstructorInjector mockConstructorInjector;

    @BeforeEach
    public void init() {
        setInternalStaticField(DelayedInjectableDependency.class, "instances", new HashMap<Class<?>, Object>());
        injector = Mockito.spy(new DefaultInjector<>(new DelayedInjectableDependency.Factory()));
        injectables = injector.injectables;

        mockFieldInjector = mock(FieldInjector.class);
        mockConstructorInjector = mock(ConstructorInjector.class);

        when(injector.getFieldInjector())
                .thenReturn(mockFieldInjector);
        when(injector.getConstructorInjector())
                .thenReturn(mockConstructorInjector);
    }

    @Test
    public void testRegisterDependency() {
        injector.registerDependency("dependency", TestDependency.class, true);

        assertEquals(injectables.size(), 1);
        DelayedInjectableDependency proxy = injectables.get("dependency");
        assertEquals(proxy.getType(), TestDependency.class);

        // should throw an exception
        assertThrows(DependencyExistsException.class, () ->
                injector.registerDependency("dependency", TestDependency.class, true));
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
        Map<String, DelayedInjectableDependency> injectables = new HashMap<>();
        injectables.put("test", new DelayedInjectableDependency("test", TestDependency.class, injector));
        injectables.put("test1", new DelayedInjectableDependency("test1", TestSubclass.class, injector));
        injectables.put("test2", new DelayedInjectableDependency("test2", TestDependency1.class, injector));
        setInternalState(injector, "injectables", injectables);

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

    public static class TestDependency {}

    public static class TestSubclass extends TestDependency {}

    public static class TestDependency1 {}

    static class CannotInject {}
}
