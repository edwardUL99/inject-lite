package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.annotations.PreConstruct;
import io.github.edwardUL99.inject.lite.exceptions.HookException;
import io.github.edwardUL99.inject.lite.hooks.PreConstructHook;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class PreConstructHandlerTest {
    private PreConstructHandler handler;
    private InternalInjector mockInjector;

    @BeforeEach
    public void init() {
        handler = new PreConstructHandler();
        mockInjector = mock(InternalInjector.class);
    }

    @Test
    public void testGoodCaseNoInjector() {
        handler.handle(mockInjector, null, GoodCaseNoInjector.class);
        assertNull(GoodCaseNoInjector.injector);
    }

    @Test
    public void testGoodCaseInjector() {
        handler.handle(mockInjector, null, GoodCaseInjector.class);
        assertEquals(mockInjector, GoodCaseInjector.injector);
    }

    @Test
    public void testNoPreConstruct() {
        HookException exception = assertThrows(HookException.class, () ->
                handler.handle(mockInjector, null, NoPreConstruct.class));
        assertEquals("When PreConstruct is implemented, there must be 1 method called " +
                "preConstruct, with either 0 args or 1 arg of type Injector", exception.getMessage());
    }

    @Test
    public void testInvalidNumArgs() {
        HookException exception = assertThrows(HookException.class, () ->
                handler.handle(mockInjector, null, InvalidNumArgs.class));
        assertEquals("PreConstruct hook method must have 0 - 1 arguments", exception.getMessage());
    }

    @Test
    public void testInvalidArgs() {
        HookException exception = assertThrows(HookException.class, () ->
                handler.handle(mockInjector, null, InvalidArgs.class));
        assertEquals("PreConstruct hook method argument must be of type Injector", exception.getMessage());
    }

    @Test
    public void testNonStatic() {
        HookException exception = assertThrows(HookException.class, () ->
                handler.handle(mockInjector, null, NonStatic.class));
        assertEquals("PreConstruct hook method must be static", exception.getMessage());
    }

    @Test
    public void testAnnotationHook() {
        assertFalse(Annotated.called);
        handler.handle(mockInjector, null, Annotated.class);
        assertTrue(Annotated.called);
        Annotated.called = false;
    }

    private static class GoodCaseNoInjector implements PreConstructHook {
        private static Injector injector;

        public static void preConstruct() {
            injector = null;
        }
    }

    private static class GoodCaseInjector implements PreConstructHook {
        private static Injector injector;

        public static void preConstruct(Injector injector) {
            GoodCaseInjector.injector = injector;
        }
    }

    private static class NoPreConstruct implements PreConstructHook { }

    private static class InvalidNumArgs implements PreConstructHook {
        public static void preConstruct(Injector injector, String s) {}
    }

    private static class InvalidArgs implements PreConstructHook {
        public static void preConstruct(String s) {}
    }

    private static class NonStatic implements PreConstructHook {
        public void preConstruct() {}
    }

    private static class Annotated {
        private static boolean called;

        @PreConstruct
        public static void preConstruct() {
            called = true;
        }
    }
}