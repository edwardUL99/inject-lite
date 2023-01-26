package io.github.edwardUL99.inject.lite.internal.proxy;

import io.github.edwardUL99.inject.lite.internal.hooks.InjectorHooks;
import io.github.edwardUL99.inject.lite.internal.hooks.LazyInvocationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InjectionInvocationProxyTest {
    private InjectionInvocationProxy proxy;
    private InjectionMethod mockMethod;
    private InjectorHooks.HookSupport mockInjector;
    private LazyInvocationHandler mockHandler;

    @BeforeEach
    public void init() {
        mockMethod = mock(InjectionMethod.class);
        mockInjector = mock(InjectorHooks.HookSupport.class);
        mockHandler = mock(LazyInvocationHandler.class);
        proxy = new InjectionInvocationProxy(mockMethod, ToProxy.class, mockInjector, mockHandler);
    }

    @Test
    public void testInvoke() throws Exception {
        ToProxy instantiated = new ToProxy();
        instantiated.n = 1;
        when(mockMethod.inject()).thenReturn(instantiated);
        Method callMethod = ToProxy.class.getDeclaredMethod("call");

        int returned = (int) proxy.invoke(proxy, callMethod);
        assertTrue(instantiated.proxyCalled);
        assertEquals(1, returned);

        returned = (int) proxy.invoke(proxy, callMethod);
        assertTrue(instantiated.proxyCalled);
        assertEquals(1, returned);

        verify(mockMethod, times(1)).inject();
        verify(mockHandler, times(2)).setCalledMethod(callMethod);
        verify(mockHandler, times(2)).handle(mockInjector, instantiated, ToProxy.class);
    }

    public static class ToProxy {
        boolean proxyCalled;
        int n;

        public int call() {
            proxyCalled = true;

            return n;
        }
    }
}