package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencyScanner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.getInternalState;

public class InjectionContextTest {
    private static DependencyScanner oldScanner;
    private DependencyScanner mockScanner;
    private Injector mockInjector;
    private InjectorFactory mockFactory;

    @BeforeAll
    public static void staticInit() {
        oldScanner = getInternalState(InjectionContext.class, "scanner");
    }

    @AfterAll
    public static void staticTeardown() {
        InjectionContext.setScanner(oldScanner);
    }

    @BeforeEach
    @SuppressWarnings("rawtypes")
    public void init() {
        mockScanner = mock(DependencyScanner.class);
        InjectionContext.setScanner(mockScanner);

        mockInjector = mock(Injector.class);
        mockFactory = mock(InjectorFactory.class);

        InjectionContext.setInjectorFactory(mockFactory);

        when(mockFactory.create())
                .thenReturn(mockInjector);

        ((Map)getInternalState(InjectionContext.class, "singletons")).clear();
    }

    @Test
    public void testCreateInjector() {
        Injector injector = InjectionContext.createInjector();
        verify(mockScanner).scanDependencies(injector);
        verify(mockFactory).create();
    }

    @Test
    public void testSingletonInjector() {
        Injector injector = InjectionContext.getSingletonInjector();
        Injector injector1 = InjectionContext.getSingletonInjector();

        verify(mockScanner).scanDependencies(injector);
        verify(mockScanner).scanDependencies(injector1);
        verify(mockFactory).create();
    }

    @Test
    public void testSetSingletonInjector() {
        Injector injector = InjectionContext.getSingletonInjector();
        Injector injector1 = InjectionContext.getSingletonInjector();

        Injector newMock = mock(Injector.class);
        when(mockFactory.create())
                .thenReturn(newMock);

        InjectionContext.setSingletonInjector(InjectionContext.createInjector());

        verify(mockScanner).scanDependencies(injector);
        assertSame(injector, injector);
        assertSame(injector, injector1);

        injector1 = InjectionContext.getSingletonInjector();
        verify(mockScanner).scanDependencies(injector);

        assertNotSame(injector, injector1);
        verify(mockFactory, times(2)).create();
    }
}
