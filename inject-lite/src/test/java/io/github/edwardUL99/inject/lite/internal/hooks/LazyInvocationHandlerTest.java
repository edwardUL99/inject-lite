package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.annotations.LazyInvocationHook;
import io.github.edwardUL99.inject.lite.exceptions.HookException;
import io.github.edwardUL99.inject.lite.hooks.LazyInvocation;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class LazyInvocationHandlerTest {
    private InternalInjector mockInjector;
    private LazyInvocationHandler invocationHandler;

    @BeforeEach
    public void init() {
        mockInjector = mock(InternalInjector.class);
        invocationHandler = new LazyInvocationHandler();
    }

    @Test
    public void testHandle() throws Exception {
        invocationHandler.setCalledMethod(LazyTarget.class.getDeclaredMethod("call"));

        LazyTarget target = new LazyTarget();

        assertEquals(0, target.timesCalled);

        invocationHandler.handle(mockInjector, target, LazyTarget.class);
        invocationHandler.handle(mockInjector, target, LazyTarget.class);

        assertEquals(2, target.timesCalled);

        target.timesCalled = 0;
        target.callFirst = true;

        invocationHandler.handle(mockInjector, target, LazyTarget.class);
        invocationHandler.handle(mockInjector, target, LazyTarget.class);

        assertEquals(1, target.timesCalled);
    }

    @Test
    public void testHandleAnnotated() throws Exception {
        invocationHandler.setCalledMethod(Annotated.class.getDeclaredMethod("target"));

        Annotated annotated = new Annotated();
        assertEquals(0, annotated.oneTimesCalled);
        assertEquals(0, annotated.twoTimesCalled);

        invocationHandler.handle(mockInjector, annotated, Annotated.class);
        invocationHandler.handle(mockInjector, annotated, Annotated.class);

        assertEquals(1, annotated.oneTimesCalled);
        assertEquals(2, annotated.twoTimesCalled);
    }

    @Test
    public void testHandleAnnotatedError() {
        AnnotatedNoParams annotated = new AnnotatedNoParams();

        HookException exception = assertThrows(HookException.class, () ->
                invocationHandler.handle(mockInjector, annotated, AnnotatedNoParams.class));
        assertTrue(exception.getMessage().contains("2 parameters"));

        AnnotatedInvalidParameter annotated1 = new AnnotatedInvalidParameter();
        exception = assertThrows(HookException.class, () ->
                invocationHandler.handle(mockInjector, annotated1, AnnotatedInvalidParameter.class));
        assertTrue(exception.getMessage().contains("type Method"));
    }

    private static class LazyTarget implements LazyInvocation {
        private boolean callFirst;
        private int timesCalled;

        public void call() {
            timesCalled++;
        }

        @Override
        public void lazilyInvoked(Injector injector, Method method) {
            if (method.getName().equals("call")) {
                call();
            }
        }

        @Override
        public boolean onlyInvokeFirst() {
            return callFirst;
        }
    }

    private static class Annotated {
        private int oneTimesCalled;
        private int twoTimesCalled;

        @LazyInvocationHook
        public void one(Injector injector, Method method) {
            oneTimesCalled++;
        }

        @LazyInvocationHook(onlyInvokeFirst = false)
        public void two(Injector injector, Method method) {
            twoTimesCalled++;
        }

        public void target() {}
    }

    private static class AnnotatedNoParams {
        public void target() {}

        @LazyInvocationHook
        public void wrong() {}
    }

    private static class AnnotatedInvalidParameter {
        public void target() {}

        @LazyInvocationHook
        public void wrong(Injector injector, String s) {}
    }
}