package io.github.edwardUL99.inject.lite.internal.proxy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ProxiesTest {
    private ProxyHandler mockHandler;

    @BeforeEach
    public void setUp() {
        mockHandler = mock(ProxyHandler.class);
        Proxies.setHandler(mockHandler);
    }

    @AfterEach
    public void tearDown() {
        Proxies.setHandler(Proxies.DEFAULT_HANDLER);
    }

    @Test
    public void testCreateProxy() throws ReflectiveOperationException {
        ProxiedInvocationHandler mockInvocationHandler = mock(ProxiedInvocationHandler.class);
        NonFinal nonFinal = new NonFinal();
        when(mockHandler.setupProxy(NonFinal.class, mockInvocationHandler))
            .thenReturn(nonFinal);

        NonFinal returned = Proxies.createProxy(NonFinal.class, mockInvocationHandler);

        assertSame(nonFinal, returned);
        verify(mockHandler).setupProxy(NonFinal.class, mockInvocationHandler);
    }

    @Test
    public void testCreateProxyFinalClass() {
        ProxiedInvocationHandler mockInvocationHandler = mock(ProxiedInvocationHandler.class);

        assertThrows(IllegalStateException.class, () ->
                Proxies.createProxy(Final.class, mockInvocationHandler));

        verifyNoInteractions(mockHandler);
    }

    @Test
    public void testCreateInjectionProxy() throws ReflectiveOperationException {
        NonFinal nonFinal = new NonFinal();
        when(mockHandler.setupProxy(eq(NonFinal.class), any(InjectionInvocationProxy.class)))
                .thenReturn(nonFinal);

        NonFinal returned = Proxies.createInjectionProxy(NonFinal.class, null);

        assertSame(nonFinal, returned);
        verify(mockHandler).setupProxy(eq(NonFinal.class), any(InjectionInvocationProxy.class));
    }

    private static class NonFinal {}

    private static final class Final {}
}