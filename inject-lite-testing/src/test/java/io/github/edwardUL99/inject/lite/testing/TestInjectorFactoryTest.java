package io.github.edwardUL99.inject.lite.testing;

import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InjectorFactory;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestInjectorFactoryTest {
    private InternalInjector mockInjector;
    private InjectorFactory mockFactory;
    private MockedStatic<InjectionContext> mockedInjectionContext;
    private TestInjectorFactory factory;

    @BeforeEach
    public void init() {
        mockInjector = mock(InternalInjector.class);
        mockFactory = mock(InjectorFactory.class);
        mockedInjectionContext = mockStatic(InjectionContext.class);
        factory = new TestInjectorFactory(mockFactory);
    }

    @AfterEach
    public void teardown() {
        if (mockedInjectionContext != null)
            mockedInjectionContext.close();
    }

    @Test
    public void testCreateWithExistingSingletonCopy() {
        Map<String, TestDelayedInjectableDependency> dependencyMap = new HashMap<>();
        dependencyMap.put("name", new TestDelayedInjectableDependency("name", "Hello"));
        TestInjector existing = new TestInjector(mockInjector, dependencyMap);
        Map<Thread, Injector> map = new HashMap<>();
        map.put(new Thread(mock(Runnable.class)), existing);

        mockedInjectionContext.when(InjectionContext::getThreadInjectors)
                .thenReturn(map);
        when(mockFactory.create())
                .thenReturn(mockInjector);

        TestInjector returned = (TestInjector) factory.create();

        assertNotSame(existing, returned);
        assertEquals(existing.testInjectables.size(), returned.testInjectables.size());
        assertEquals(existing.testInjectables.get("name").get(), returned.testInjectables.get("name").get());

        mockedInjectionContext.verify(InjectionContext::getThreadInjectors);
        verify(mockFactory).create();
    }

    @Test
    public void testCreateEmptyTestInjector() {
        mockedInjectionContext.when(InjectionContext::getThreadInjectors)
                .thenReturn(new HashMap<>());
        when(mockFactory.create())
                .thenReturn(mockInjector);

        TestInjector returned = (TestInjector) factory.create();

        assertNotNull(returned);
        assertEquals(0, returned.testInjectables.size());

        mockedInjectionContext.verify(InjectionContext::getThreadInjectors);
        verify(mockFactory).create();
    }
}
