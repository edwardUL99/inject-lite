package io.github.edwardUL99.inject.lite.internal.proxy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InjectionInvocationProxyTest {
    private InjectionInvocationProxy proxy;
    private InjectionMethod mockMethod;

    @BeforeEach
    public void init() {
        mockMethod = mock(InjectionMethod.class);
        proxy = new InjectionInvocationProxy(mockMethod, ToProxy.class);
    }

    @Test
    public void testInvoke() throws Exception {
        ToProxy instantiated = new ToProxy();
        instantiated.n = 1;
        when(mockMethod.inject()).thenReturn(instantiated);

        int returned = (int) proxy.invoke(proxy, ToProxy.class.getDeclaredMethod("call"));
        assertTrue(instantiated.proxyCalled);
        assertEquals(1, returned);

        returned = (int) proxy.invoke(proxy, ToProxy.class.getDeclaredMethod("call"));
        assertTrue(instantiated.proxyCalled);
        assertEquals(1, returned);

        verify(mockMethod, times(1)).inject();
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