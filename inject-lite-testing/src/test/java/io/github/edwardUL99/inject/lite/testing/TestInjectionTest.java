package io.github.edwardUL99.inject.lite.testing;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.DefaultAnnotationScanner;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.ScannersContext;
import io.github.edwardUL99.inject.lite.internal.injector.DefaultInjectorFactory;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.threads.Threads;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestInjectionTest {
    private MockedStatic<?>[] statics;
    private MockedStatic<InjectionContext> injectionContext;
    private MockedStatic<Injection> injection;
    private MockedStatic<Threads> threads;
    private MockedStatic<ScannersContext> scannersContext;
    private TestInjector mockInjector;

    @BeforeEach
    public void init() {
        injectionContext = mockStatic(InjectionContext.class);
        injection = mockStatic(Injection.class);
        threads = mockStatic(Threads.class);
        scannersContext = mockStatic(ScannersContext.class);

        statics = new MockedStatic<?>[]{injectionContext, injection, threads, scannersContext};

        mockInjector = mock(TestInjector.class);
    }

    @AfterEach
    public void teardown() {
        for (MockedStatic<?> mocked : statics)
            mocked.close();
    }

    @Test
    public void testStart() {
        injectionContext.when(Injection::globalInjector)
                .thenReturn(mockInjector);

        StubTestClass testClass = new StubTestClass();
        assertNull(testClass.value);
        assertNull(testClass.real);
        assertNull(testClass.testInject);

        FieldInjector mockFieldInjector = mock(FieldInjector.class);

        when(mockInjector.instantiate(Integer.class))
                .thenReturn(42);
        when(mockInjector.getFieldInjector())
                .thenReturn(mockFieldInjector);

        TestInjection.start(testClass);

        assertNotNull(testClass.value);
        assertNotNull(testClass.testInject);
        verify(mockInjector).registerTestDependency("value", testClass.value);
        verify(mockInjector).instantiate(Integer.class);
        verify(mockInjector).getFieldInjector();
        verify(mockFieldInjector).injectFields(testClass);

        injectionContext.verify(() -> InjectionContext.setInjectorFactory(any(TestInjectorFactory.class)));
        injection.verify(Injection::globalInjector);
        scannersContext.verify(() -> ScannersContext.setSingletonScanner(any(DefaultAnnotationScanner.class)));
    }

    @Test
    public void testEnd() {
        Thread thread = new Thread(mock(Runnable.class));

        threads.when(Threads::getCurrentThread)
                    .thenReturn(thread);

        TestInjection.end();

        threads.verify(Threads::getCurrentThread);
        injectionContext.verify(() -> InjectionContext.destroySingletonInjector(thread));
        scannersContext.verify(() -> ScannersContext.destroySingletonScanner(thread));
        injectionContext.verify(() -> InjectionContext.setInjectorFactory(any(DefaultInjectorFactory.class)));
    }

    private static class StubTestClass {
        @MockDependency("value")
        private TestMock value;

        @Inject("real")
        private TestReal real;

        @TestInject
        private Integer testInject;
    }

    private static class TestMock {}

    private static class TestReal {}
}
