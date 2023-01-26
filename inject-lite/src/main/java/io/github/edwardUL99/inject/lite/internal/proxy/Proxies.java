package io.github.edwardUL99.inject.lite.internal.proxy;

import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.lang.reflect.Modifier;

/**
 * Helper class for creating proxies
 */
public final class Proxies {
    /**
     * Default handler to use in non-test code, the setHandler should only be used in tests and be called with this at
     * the end
     */
    static final ProxyHandler DEFAULT_HANDLER = new ByteBuddyProxyHandler();
    /**
     * The handler for creating proxies
     */
    private static ProxyHandler handler = DEFAULT_HANDLER;

    /**
     * Set the handler instance to use
     * @param handler the handler instance
     */
    static void setHandler(ProxyHandler handler) {
        Proxies.handler = handler;
    }

    /**
     * Create a proxied instance
     * @param proxiedType the type of the proxy
     * @param proxy the dynamic proxy that forwards the request
     * @return the proxied instance
     */
    public static <T> T createProxy(Class<T> proxiedType, ProxiedInvocationHandler proxy) throws ReflectiveOperationException {
        if (Modifier.isFinal(proxiedType.getModifiers())) throw new IllegalStateException("Cannot proxy a final type: " + proxiedType);

        return handler.setupProxy(proxiedType, proxy);
    }

    /**
     * Create a proxy that is injected on first method call to it
     * @param proxiedType the type of the proxy
     * @param injectionMethod the method to inject the dependency
     * @param injector the injector instance
     * @return the proxied type
     */
    public static <T> T createInjectionProxy(Class<T> proxiedType, InjectionMethod injectionMethod, InternalInjector injector)
            throws ReflectiveOperationException {
        return createProxy(proxiedType, new InjectionInvocationProxy(injectionMethod, proxiedType, injector));
    }
}
