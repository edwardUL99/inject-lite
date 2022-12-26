package io.github.edwardUL99.inject.lite.internal.proxy;

import java.lang.reflect.Method;

/**
 * This interface represents a handler of an invocation of a method on a proxied object
 */
public interface ProxiedInvocationHandler {
    /**
     * Invoke the proxy method on the given proxy
     * @param proxy the proxy object
     * @param method the method called
     * @param arguments the arguments passed to the method
     * @return the return value
     */
    Object invoke(Object proxy, Method method, Object...arguments) throws ReflectiveOperationException;
}
