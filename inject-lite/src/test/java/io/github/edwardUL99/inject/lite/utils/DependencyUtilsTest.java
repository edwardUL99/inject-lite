package io.github.edwardUL99.inject.lite.utils;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.exceptions.AmbiguousDependencyException;
import io.github.edwardUL99.inject.lite.internal.injector.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.utils.DependencyUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DependencyUtilsTest {
    private InternalInjector<DelayedInjectableDependency> mockInjector;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void init() {
        mockInjector = mock(InternalInjector.class);
    }

    @AfterEach
    public void teardown() {
        Injection.configuration.setRequireNamedMultipleMatch(false);
    }

    @Test
    public void testAmbiguityAllowed() {
        DependencyUtils.getUnnamedDependency(String.class, mockInjector);
        verify(mockInjector).getInjectableDependency(String.class);
    }

    @Test
    public void testNoAmbiguityAllowedWithOneDependency() {
        Injection.configuration.setRequireNamedMultipleMatch(true);
        List<DelayedInjectableDependency> dependencies = new ArrayList<>();
        DelayedInjectableDependency mockDependency = mock(DelayedInjectableDependency.class);
        dependencies.add(mockDependency);

        when(mockInjector.getInjectableDependencies(String.class))
                .thenReturn(dependencies);

        DelayedInjectableDependency dependency = DependencyUtils.getUnnamedDependency(String.class, mockInjector);

        assertEquals(dependency, mockDependency);
        verify(mockInjector).getInjectableDependencies(String.class);
    }

    @Test
    public void testNoAmbiguityAllowedWithOneDependencies() {
        Injection.configuration.setRequireNamedMultipleMatch(true);

        when(mockInjector.getInjectableDependencies(String.class))
                .thenReturn(null);

        DelayedInjectableDependency dependency = DependencyUtils.getUnnamedDependency(String.class, mockInjector);

        assertNull(dependency);
        verify(mockInjector).getInjectableDependencies(String.class);
    }

    @Test
    public void testNoAmbiguityAllowedWithMultipleDependency() {
        Injection.configuration.setRequireNamedMultipleMatch(true);
        List<DelayedInjectableDependency> dependencies = new ArrayList<>();
        dependencies.add(mock(DelayedInjectableDependency.class));
        dependencies.add(mock(DelayedInjectableDependency.class));

        when(mockInjector.getInjectableDependencies(String.class))
                .thenReturn(dependencies);

        assertThrows(AmbiguousDependencyException.class, () ->
                DependencyUtils.getUnnamedDependency(String.class, mockInjector));

        verify(mockInjector).getInjectableDependencies(String.class);
    }
}
