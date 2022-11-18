package io.github.edwardUL99.inject.lite;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.threads.ExecutorServiceExecutor;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class InjectionTest {
    private MockedStatic<InjectionContext> mockedContext;

    @BeforeEach
    public void init() {
        mockedContext = mockStatic(InjectionContext.class);
    }

    @AfterEach
    public void teardown() {
        if (mockedContext != null)
            mockedContext.close();
    }
    @Test
    public void testNewInjector() {
        mockedContext.when(InjectionContext::createInjector)
                .thenReturn(mock(Injector.class));

        Injector injector = Injection.newInjector();

        assertNotNull(injector);
    }

    @Test
    public void testGlobalInjector() {
        mockedContext.when(InjectionContext::getSingletonInjector)
                .thenReturn(mock(Injector.class));

        Injector injector = Injection.globalInjector();
        Injector injector1 = Injection.globalInjector();

        assertSame(injector, injector1);
    }

    @Test
    public void testSharedInjectionExecutor() {
        AsynchronousExecutor executor = Injection.sharedInjectionExecutor();
        assertInstanceOf(ExecutorServiceExecutor.class, executor);
    }

    @Injectable("test")
    public static class TestDependency { }
}
