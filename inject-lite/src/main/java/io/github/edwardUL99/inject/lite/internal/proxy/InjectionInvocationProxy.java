package io.github.edwardUL99.inject.lite.internal.proxy;

import io.github.edwardUL99.inject.lite.exceptions.InjectionException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for invoking a proxy instance using injection
 */
public class InjectionInvocationProxy implements ProxiedInvocationHandler {
    /**
     * The method to inject the dependency
     */
    private final InjectionMethod injectionMethod;
    /**
     * The map of methods including superclass methods
     */
    private final Map<String, Method> methods = new HashMap<>();
    /**
     * The instantiated object
     */
    private Object instantiated;

    /**
     * Create an invocation handler
     * @param injectionMethod the injection method
     */
    public InjectionInvocationProxy(InjectionMethod injectionMethod, Class<?> type) {
        this.injectionMethod = injectionMethod;
        instantiateMethods(type);
        instantiateMethods(Object.class); // object base methods
    }

    private void instantiateMethods(Class<?> cls) {
        if (cls != null) {
            for (Method method : cls.getDeclaredMethods())
                methods.put(method.getName(), method);
            instantiateMethods(cls.getSuperclass());
        }
    }

    private Object instantiateIfNull() {
        if (instantiated == null) instantiated = injectionMethod.inject();

        return instantiated;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object... arguments) throws ReflectiveOperationException {
        String name = method.getName();
        Method declared = methods.get(name);

        if (declared == null) throw new InjectionException("Failed to proxy request to Lazy dependency since an unknown " +
                "method: " + name + " was called");

        return methods.get(method.getName()).invoke(instantiateIfNull(), arguments);
    }
}
