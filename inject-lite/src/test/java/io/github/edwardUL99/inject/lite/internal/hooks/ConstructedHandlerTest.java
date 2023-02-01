package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.annotations.Constructed;
import io.github.edwardUL99.inject.lite.exceptions.HookException;
import io.github.edwardUL99.inject.lite.hooks.ConstructedHook;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ConstructedHandlerTest {
    private InternalInjector mockInjector;
    private ConstructedHandler handler;
    private ConstructedHook postConstruct;

    @BeforeEach
    public void init() {
        mockInjector = mock(InternalInjector.class);
        handler = new ConstructedHandler();
        postConstruct = spy(new ConstructedHook() {
            @Override
            public void constructed(Injector injector) {

            }
        });
    }

    @Test
    public void testHandle() {
        handler.handle(mockInjector, postConstruct, ConstructedHook.class);

        verify(postConstruct).constructed(mockInjector);
    }

    @Test
    public void testHandleHook() {
        Annotated annotated = new Annotated();
        assertFalse(annotated.called);
        handler.handle(mockInjector, annotated, Annotated.class);
        assertTrue(annotated.called);
    }

    @Test
    public void testHandleHookErrors() {
        AnnotatedErrorNoParamsTest annotated = new AnnotatedErrorNoParamsTest();

        HookException exception = assertThrows(HookException.class, () ->
                handler.handle(mockInjector, annotated, AnnotatedErrorNoParamsTest.class));
        assertTrue(exception.getMessage().contains("1 parameter"));

        AnnotatedErrorWrongParamTest annotated1 = new AnnotatedErrorWrongParamTest();
        exception = assertThrows(HookException.class, () ->
                handler.handle(mockInjector, annotated1, AnnotatedErrorWrongParamTest.class));
        assertTrue(exception.getMessage().contains("type Injector"));
    }

    private static class Annotated {
        private boolean called;

        @Constructed
        public void constructed(Injector injector) {
            called = true;
        }
    }

    private static class AnnotatedErrorNoParamsTest {

        @Constructed
        public void constructed() {
        }
    }

    private static class AnnotatedErrorWrongParamTest {

        @Constructed
        public void constructed(String s) {
        }
    }
}