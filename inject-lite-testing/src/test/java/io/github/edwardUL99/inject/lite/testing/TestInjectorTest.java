package io.github.edwardUL99.inject.lite.testing;

import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.injector.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class TestInjectorTest {
    private TestInjector injector;
    private InternalInjector<DelayedInjectableDependency> mockInjector;
    private Map<String, TestDelayedInjectableDependency> testInjectables;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void init() {
        mockInjector = mock(InternalInjector.class);
        injector = new TestInjector(mockInjector);
        testInjectables = injector.testInjectables;
    }

    @Test
    public void testRegisterTestDependency() {
        Object instance = "";
        injector.registerTestDependency("name", instance);

        TestDelayedInjectableDependency proxy = testInjectables.get("name");

        assertNotNull(proxy);
        assertEquals("", proxy.get());
    }

    @Test
    public void testRegisterNormalDependency() {
        DelayedInjectableDependency proxy = new DelayedInjectableDependency("name", String.class, null);
        injector.registerInjectableDependency(proxy);
        DelayedInjectableDependency proxy1 = testInjectables.get("name");
        assertNull(proxy1);

        verify(mockInjector).registerInjectableDependency(proxy);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testActOnDependencies() {
        String instance = "";
        injector.registerTestDependency("name", instance);
        Consumer<String> mockConsumer = (Consumer<String>) mock(Consumer.class);

        injector.actOnDependencies(mockConsumer, String.class);

        verify(mockConsumer).accept(instance);
    }

    @Test
    public void testRegisterDependency() {
        injector.registerDependency("name", String.class, true);
        verify(mockInjector).registerDependency("name", String.class, true);
    }

    @Test
    public void testInjectionByName() {
        String instance = "";
        injector.registerTestDependency("name", instance);

        String returned = injector.inject("name", String.class);

        assertEquals(returned, instance);

        when(mockInjector.inject("name1", String.class))
                .thenReturn("Hello");

        returned = injector.inject("name1", String.class);

        assertEquals("Hello", returned);
        verify(mockInjector).inject("name1", String.class);
    }

    @Test
    public void testInjectionByType() {
        String instance = "";
        injector.registerTestDependency("name", instance);

        String returned = injector.inject(String.class);

        assertEquals(returned, instance);

        List<Integer> wrappedReturned = new ArrayList<>();
        wrappedReturned.add(1);
        when(mockInjector.injectAll(Integer.class))
                .thenReturn(wrappedReturned);

        Integer returned1 = injector.inject(Integer.class);

        assertEquals(1, returned1);
        verify(mockInjector).injectAll(Integer.class);
    }

    @Test
    public void testInstantiate() {
        TestInjector spied = spy(injector);
        ConstructorInjector mockConstructor = mock(ConstructorInjector.class);
        FieldInjector mockResource = mock(FieldInjector.class);

        when(spied.getConstructorInjector())
                .thenReturn(mockConstructor);
        when(spied.getFieldInjector())
                .thenReturn(mockResource);

        String obj = "Hello";
        when(mockConstructor.injectConstructor("String", String.class))
                .thenReturn(obj);

        String instantiated = spied.instantiate(String.class);

        assertEquals(instantiated, obj);
        verify(mockConstructor).injectConstructor("String", String.class);
        verify(mockResource).injectFields(obj);
        verify(spied, times(2)).getConstructorInjector();
        verify(spied).getFieldInjector();
    }

    @Test
    public void testGetInjectionProxy() {
        injector.registerTestDependency("name", new TestClass());

        DelayedInjectableDependency proxy = injector.getInjectableDependency(Interface.class);

        assertNotNull(proxy);
        assertEquals(TestClass.class, proxy.getType());
        verifyNoInteractions(mockInjector);

        injector.testInjectables.clear();

        DelayedInjectableDependency mockProxy = mock(DelayedInjectableDependency.class);

        when(mockInjector.getInjectableDependency(Interface.class))
                .thenReturn(mockProxy);

        proxy = injector.getInjectableDependency(Interface.class);

        assertNotNull(proxy);
        assertEquals(proxy, mockProxy);
        verify(mockInjector).getInjectableDependency(Interface.class);
    }

    @Test
    public void testInjectAll() {
        TestClass testClass = new TestClass();
        TestClass1 testClass1 = new TestClass1();
        List<Interface> returnedList = new ArrayList<>();
        returnedList.add(testClass1);

        when(mockInjector.injectAll(Interface.class))
                .thenReturn(returnedList);

        injector.registerTestDependency("name", testClass);
        injector.registerDependency("name1", TestClass1.class, true);

        List<Interface> all = injector.injectAll(Interface.class);

        assertEquals(2, all.size());
        assertEquals(all.get(0), testClass);
        assertEquals(all.get(1), testClass1);
    }

    private interface Interface {}

    private static class TestClass implements Interface {}

    private static class TestClass1 extends TestClass {}
}
