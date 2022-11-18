package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class BaseFieldInjectorTest {
    private BaseFieldInjector baseInjector;
    private Injector mockInjector;

    @BeforeEach
    public void init() {
        mockInjector = mock(Injector.class);
        // we use single level here for sake of instantiation. We only want to test features of base field injector
        // so the concrete implementation doesn't matter as long as we test field functionality
        baseInjector = new SingleLevelFieldInjector(mockInjector);
    }

    @Test
    public void testInjectFields() {
        when(mockInjector.inject("value", String.class))
                .thenReturn("Hello World");
        Injectable injectable = new Injectable();

        assertNull(injectable.value);

        baseInjector.injectFields(injectable);

        assertEquals("Hello World", injectable.value);
        verify(mockInjector).inject("value", String.class);
    }

    @Test
    public void testInjectFinalField() {
        FinalResource finalResource = new FinalResource();

        assertThrows(InjectionException.class, () ->
                baseInjector.injectFields(finalResource));
        verifyNoInteractions(mockInjector);
    }

    @Test
    public void testInvalidTypeField() {
        when(mockInjector.inject(eq("value"), any()))
                .thenReturn(42);
        Injectable injectable = new Injectable();

        assertThrows(InjectionException.class, () ->
                baseInjector.injectFields(injectable));
        verify(mockInjector).inject(eq("value"), any());
    }

    // a class with a valid resource annotation
    private static class Injectable {
        @Inject("value")
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
