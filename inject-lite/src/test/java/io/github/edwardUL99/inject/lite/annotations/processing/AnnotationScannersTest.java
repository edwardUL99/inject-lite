package io.github.edwardUL99.inject.lite.annotations.processing;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.ScannersContext;
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

public class AnnotationScannersTest {
    private MockedStatic<Injection> mockedInjection;
    private MockedStatic<ScannersContext> mockedContext;
    private Injector mockInjector;

    @BeforeEach
    public void setup() {
        mockedContext = mockStatic(ScannersContext.class);
        mockedInjection = mockStatic(Injection.class);
        mockInjector = mock(InternalInjector.class);
        mockedInjection.when(Injection::globalInjector)
                .thenReturn(mockInjector);
    }

    @AfterEach
    public void teardown() {
        if (mockedContext != null)
            mockedContext.close();

        if (mockedInjection != null)
            mockedInjection.close();
    }
    @Test
    public void testNewScanner() {
        mockedContext.when(() -> ScannersContext.createScanner(mockInjector))
                .thenReturn(mock(AnnotationScanner.class));
        AnnotationScanner scanner = AnnotationScanners.newScanner();
        assertNotNull(scanner);
    }

    @Test
    public void testGlobalScanner() {
        mockedContext.when(ScannersContext::getSingletonScanner)
                .thenReturn(mock(AnnotationScanner.class));

        AnnotationScanner scanner = AnnotationScanners.globalScanner();
        AnnotationScanner scanner1 = AnnotationScanners.globalScanner();

        assertSame(scanner, scanner1);
    }

    @Test
    public void testSharedScannerExecutor() {
        AsynchronousExecutor executor = AnnotationScanners.sharedScannerExecutor();
        assertInstanceOf(ExecutorServiceExecutor.class, executor);
    }
}
