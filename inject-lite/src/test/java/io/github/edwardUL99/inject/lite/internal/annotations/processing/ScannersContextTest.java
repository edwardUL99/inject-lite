package io.github.edwardUL99.inject.lite.internal.annotations.processing;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationScanner;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class ScannersContextTest {
    private MockedStatic<Injection> mockedInjection;
    private InternalInjector mockInjector;

    @BeforeEach
    public void setup() {
        mockedInjection = mockStatic(Injection.class);
        mockInjector = mock(InternalInjector.class);
        mockedInjection.when(Injection::globalInjector)
                .thenReturn(mockInjector);
    }

    @AfterEach
    public void teardown() {
        if (mockedInjection != null)
            mockedInjection.close();
    }

    @Test
    public void testCreateScanner() {
        AnnotationScanner scanner = ScannersContext.createScanner();
        assertInstanceOf(DefaultAnnotationScanner.class, scanner);
    }

    @Test
    public void testSingletonScanner() {
        AnnotationScanner scanner = ScannersContext.getSingletonScanner();
        AnnotationScanner scanner1 = ScannersContext.getSingletonScanner();

        assertSame(scanner, scanner1);
    }

    @Test
    public void testSetSingletonScanner() {
        AnnotationScanner scanner = ScannersContext.getSingletonScanner();
        AnnotationScanner scanner1 = ScannersContext.getSingletonScanner();

        assertSame(scanner, scanner1);

        ScannersContext.setSingletonScanner(new DefaultAnnotationScanner(mockInjector));

        scanner1 = ScannersContext.getSingletonScanner();

        assertNotSame(scanner, scanner1);
    }
}
