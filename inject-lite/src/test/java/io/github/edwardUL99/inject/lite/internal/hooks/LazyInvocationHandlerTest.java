package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.hooks.LazyInvocation;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class LazyInvocationHandlerTest {
    private LazyInvocationHandler invocationHandler;

    @BeforeEach
    public void init() throws Exception {
        invocationHandler = new LazyInvocationHandler();
        invocationHandler.setCalledMethod(LazyTarget.class.getDeclaredMethod("call"));
    }

    @Test
    public void testHandle() {
        InternalInjector mockInjector = mock(InternalInjector.class);
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
}