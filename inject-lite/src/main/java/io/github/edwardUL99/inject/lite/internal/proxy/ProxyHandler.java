package io.github.edwardUL99.inject.lite.internal.proxy;

/**
 * Represents a class that can setup proxies. This interface is an attempt to uncouple the proxy library implementation
 * from the API to create a proxy (e.g. could have a handler for creating ByteBuddy proxies, Javaassist, etc.)
 */
public interface ProxyHandler {
    /**
     * Handle the proxy for the given type
     * @param proxiedType the type being proxied, will be extended with the proxy being the subclass
     * @param proxy the proxy object accepting the requests
     * @return the proxied object
     */
    <T> T setupProxy(Class<T> proxiedType, ProxiedInvocationHandler proxy) throws ReflectiveOperationException;
}
