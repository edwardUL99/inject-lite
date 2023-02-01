package io.github.edwardUL99.inject.lite.internal.proxy;

import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.internal.hooks.InjectorHooks;
import io.github.edwardUL99.inject.lite.internal.hooks.LazyInvocationHandler;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

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
     * The injector instance
     */
    private final InternalInjector injector;
    /**
     * Determines if hook support is available
     */
    private final boolean hookSupport;
    /**
     * The map of methods including superclass methods
     */
    private final Map<String, Method> methods = new HashMap<>();
    /**
     * A lazy invocation handler for lazy invocation hooks
     */
    private final LazyInvocationHandler lazyInvocationHandler;
    /**
     * The instantiated object
     */
    private Object instantiated;

    /**
     * Create an invocation handler
     * @param injectionMethod the injection method
     * @param type class type
     * @param injector injector for this class
     */
    public InjectionInvocationProxy(InjectionMethod injectionMethod, Class<?> type, InternalInjector injector) {
        this(injectionMethod, type, injector, new LazyInvocationHandler());
    }

    /**
     * Package private constructor to allow injection of custom LazyInvocationHandler
     * @param injectionMethod the injection method
     * @param type class type
     * @param injector injector for this class
     * @param lazyInvocationHandler the handler instance
     */
    InjectionInvocationProxy(InjectionMethod injectionMethod, Class<?> type, InternalInjector injector,
                                       LazyInvocationHandler lazyInvocationHandler) {
        this.injectionMethod = injectionMethod;
        this.injector = injector;
        this.hookSupport = this.injector instanceof InjectorHooks.HookSupport;
        this.lazyInvocationHandler = lazyInvocationHandler;
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
        if (this.hookSupport) lazyInvocationHandler.setCalledMethod(method);
        String name = method.getName();
        Method declared = methods.get(name);

        if (declared == null) throw new InjectionException("Failed to proxy request to Lazy dependency since an unknown " +
                "method: " + name + " was called");

        Object instantiated = instantiateIfNull();

        if (this.hookSupport) lazyInvocationHandler.handle(injector, instantiated, instantiated.getClass());

        return methods.get(method.getName()).invoke(instantiated, arguments);
    }
}
