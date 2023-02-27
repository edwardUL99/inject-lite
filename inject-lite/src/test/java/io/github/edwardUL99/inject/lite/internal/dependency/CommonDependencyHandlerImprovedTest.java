package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.annotations.Principal;
import io.github.edwardUL99.inject.lite.exceptions.AmbiguousDependencyException;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
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

public class CommonDependencyHandlerImprovedTest {
    private CommonDependencyHandlerImproved handler;
    private InternalInjector mockInjector;

    @BeforeEach
    public void setUp() {
        mockInjector = mock(InternalInjector.class);
        handler = new CommonDependencyHandlerImproved(mockInjector);
    }

    @SafeVarargs
    private final List<InjectableDependency> getDependencies(Class<? extends Parent>... objects) {
        List<InjectableDependency> list = new ArrayList<>(objects.length);

        for (Class<? extends Parent> parent : objects)
            list.add(new DelayedInjectableDependency("name", parent, mockInjector));

        return list;
    }

    @Test
    public void testGetUnnamedDependencyNormal() {
        when(mockInjector.getInjectableDependencies(Parent.class))
                .thenReturn(getDependencies(PrincipalAnnotated.class));

        InjectableDependency returned = handler.getUnnamedDependency(Parent.class);

        assertEquals(PrincipalAnnotated.class, returned.getType());
        verify(mockInjector).getInjectableDependencies(Parent.class);
    }

    @Test
    public void testGetUnnamedNull() {
        when(mockInjector.getInjectableDependencies(Parent.class))
                .thenReturn(null);

        InjectableDependency returned = handler.getUnnamedDependency(Parent.class);

        assertNull(returned);
        verify(mockInjector).getInjectableDependencies(Parent.class);
    }

    @Test
    public void testGetUnnamedOnePrincipal() {
        when(mockInjector.getInjectableDependencies(Parent.class))
                .thenReturn(getDependencies(Duplicate1.class, Duplicate2.class, PrincipalAnnotated.class));

        InjectableDependency returned = handler.getUnnamedDependency(Parent.class);

        assertEquals(PrincipalAnnotated.class, returned.getType());
        verify(mockInjector).getInjectableDependencies(Parent.class);
    }

    @Test
    public void testGetUnnamedMultiplePrincipal() {
        when(mockInjector.getInjectableDependencies(Parent.class))
                .thenReturn(getDependencies(Duplicate1.class, DuplicatePrincipalAnnotated.class, Duplicate2.class, PrincipalAnnotated.class));

        assertThrows(AmbiguousDependencyException.class, () ->
                handler.getUnnamedDependency(Parent.class));

        verify(mockInjector).getInjectableDependencies(Parent.class);
    }

    @Test
    public void testGetUnnamedNoPrincipal() {
        when(mockInjector.getInjectableDependencies(Parent.class))
                .thenReturn(getDependencies(Duplicate1.class, Duplicate2.class));

        assertThrows(AmbiguousDependencyException.class, () ->
                handler.getUnnamedDependency(Parent.class));

        verify(mockInjector).getInjectableDependencies(Parent.class);
    }

    private interface Parent {}

    @Principal
    private static class PrincipalAnnotated implements Parent {}

    @Principal
    private static class DuplicatePrincipalAnnotated implements Parent {}

    private static class Duplicate1 implements Parent {}

    private static class Duplicate2 implements Parent {}
}