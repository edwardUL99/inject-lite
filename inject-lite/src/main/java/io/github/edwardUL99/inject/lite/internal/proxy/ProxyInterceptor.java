package io.github.edwardUL99.inject.lite.internal.proxy;

import java.lang.reflect.Method;

/**
 * Represents a base interceptor for intercepting methods invoked on a proxy
 */
public abstract class ProxyInterceptor {
    /**
     * The handler for the proxy interceptor invocation
     */
    private final ProxiedInvocationHandler proxy;

    /**
     * Create an interceptor which forwards requests to the provided proxy handler
     * @param proxy the handler
     */
    public ProxyInterceptor(ProxiedInvocationHandler proxy) {
        this.proxy = proxy;
    }

    /**
     * Performs the interception, this method should be called by the subclass hook which hooks into the implementation
     * library's proxy mechanism
     * @param proxy the proxy object
     * @param method the method called
     * @param arguments the arguments passed to the method
     * @return the return value
     */
    public final Object intercept(Object proxy, Method method, Object... arguments) throws ReflectiveOperationException {
        return this.proxy.invoke(proxy, method, arguments);
    }
}
