package io.github.edwardUL99.inject.lite.internal.proxy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ByteBuddyProxyHandlerTest {
    private ByteBuddyProxyHandler handler;

    @BeforeEach
    public void init() {
        handler = new ByteBuddyProxyHandler();
    }

    @Test
    public void testSetupProxy() throws ReflectiveOperationException {
        ToProxy toProxy = new ToProxy();
        TestInvocationHandler proxyHandler = new TestInvocationHandler(toProxy);

        ToProxy proxy = handler.setupProxy(ToProxy.class, proxyHandler);

        assertFalse(toProxy.proxyCalled);
        assertFalse(proxyHandler.proxied);
        assertInstanceOf(ByteBuddyProxy.class, proxy);

        proxy.call();

        assertTrue(toProxy.proxyCalled);
        assertTrue(proxyHandler.proxied);
    }

    @Test
    public void testSetupProxyConstructor() throws ReflectiveOperationException {
        ToProxyConstructor toProxy = new ToProxyConstructor("");
        TestInvocationHandler proxyHandler = new TestInvocationHandler(toProxy);

        ToProxyConstructor proxy = handler.setupProxy(ToProxyConstructor.class, proxyHandler);

        assertFalse(toProxy.proxyCalled);
        assertFalse(proxyHandler.proxied);
        assertInstanceOf(ByteBuddyProxy.class, proxy);

        proxy.call();

        assertTrue(toProxy.proxyCalled);
        assertTrue(proxyHandler.proxied);
    }

    public static class ToProxy {
        boolean proxyCalled;

        public void call() {
            proxyCalled = true;
        }
    }

    public static class ToProxyConstructor {
        boolean proxyCalled;

        public ToProxyConstructor(String s) {}

        public void call() {
            proxyCalled = true;
        }
    }

    private static class TestInvocationHandler implements ProxiedInvocationHandler {
        private final Object target;
        boolean proxied;

        public TestInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object... arguments) throws ReflectiveOperationException {
            proxied = true;
            return target.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes())
                    .invoke(target, arguments);
        }
    }
}