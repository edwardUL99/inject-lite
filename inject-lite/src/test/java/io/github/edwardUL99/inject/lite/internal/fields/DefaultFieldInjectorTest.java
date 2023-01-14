package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Optional;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.internal.dependency.CommonDependencyHandler;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class DefaultFieldInjectorTest {
    private DefaultFieldInjector baseInjector;
    private InternalInjector mockInjector;
    private CommonDependencyHandler mockHandler;

    @BeforeEach
    public void init() {
        mockInjector = mock(InternalInjector.class);
        baseInjector = new DefaultFieldInjector(mockInjector);

        mockHandler = mock(CommonDependencyHandler.class);
        baseInjector.setDependencyHandler(mockHandler);

        when(mockHandler.getDependencyCheckingLazy(any(), any(), any()))
                .thenCallRealMethod();
    }

    @Test
    public void testInjectFields() {
        when(mockInjector.injectWithGraph("value", String.class))
                .thenReturn("Hello World");
        Injectable injectable = new Injectable();

        assertNull(injectable.value);

        baseInjector.injectFields(injectable);

        assertEquals("Hello World", injectable.value);
        verify(mockInjector).injectWithGraph("value", String.class);
        verify(mockHandler).getDependencyCheckingLazy(any(), any(), any());
    }

    @Test
    public void testInjectFieldsOptional() {
        doThrow(DependencyNotFoundException.class).when(mockInjector)
                .injectWithGraph("value", String.class);
        Injectable injectable = new Injectable();

        assertNull(injectable.value);

        baseInjector.injectFields(injectable);

        assertNull(injectable.value);
        verify(mockInjector).injectWithGraph("value", String.class);
        verify(mockHandler).getDependencyCheckingLazy(any(), any(), any());
    }

    @Test
    public void testInjectFinalField() {
        FinalResource finalResource = new FinalResource();

        assertThrows(InjectionException.class, () ->
                baseInjector.injectFields(finalResource));
        verifyNoInteractions(mockInjector);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidTypeField() {
        when(mockInjector.getInjectableDependency(any(Class.class), any(Supplier.class), any(boolean.class)))
                .thenReturn(mock(InjectableDependency.class));

        when(mockInjector.injectWithGraph(eq("value"), any()))
                .thenReturn(42);
        Injectable injectable = new Injectable();

        assertThrows(InjectionException.class, () ->
                baseInjector.injectFields(injectable));
        verify(mockInjector).injectWithGraph(eq("value"), any());
    }

    // a class with a valid resource annotation
    private static class Injectable {
        @Inject("value")
        @Optional
        private String value;
    }

    // a class with a final field attempted to be injected
    private static class FinalResource {
        @Inject("value")
        private final String value;

        private FinalResource() {
            value = "";
        }
    }
}
