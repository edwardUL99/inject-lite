package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.annotations.LazyInvocationHook;
import io.github.edwardUL99.inject.lite.exceptions.HookException;
import io.github.edwardUL99.inject.lite.hooks.LazyInvocation;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * A handler for lazy invocation
 */
public class LazyInvocationHandler extends BaseHookHandler {
    private boolean called;
    private Method calledMethod;
    private final List<Method> hooksCalled = new ArrayList<>();

    @Override
    protected Class<? extends Hook> getHookType() {
        return LazyInvocation.class;
    }

    /**
     * Set the called method on the lazy invocation. Passed into the hook on the next call to the hook
     * @param method the called method
     */
    public void setCalledMethod(Method method) {
        calledMethod = method;
    }

    @Override
    protected Class<? extends Annotation> getAnnotationHook() {
        return LazyInvocationHook.class;
    }

    @Override
    protected String validateAnnotationHookParameters(Method method) {
        Parameter[] parameters = method.getParameters();

        if (parameters.length != 2) {
            return "Must have 2 parameters";
        } else if (!parameters[0].getType().equals(Injector.class) || !parameters[1].getType().equals(Method.class)) {
            return "Parameter 0 must be of type Injector and parameter 1 must be of type Method";
        } else {
            return null;
        }
    }

    @Override
    protected void handleAnnotatedMethods(InternalInjector injector, Object instance, List<Method> methods) {
        try {
            for (Method m : methods) {


                LazyInvocationHook hook = m.getAnnotation(LazyInvocationHook.class);

                if (hook.onlyInvokeFirst()) {
                    if (!hooksCalled.contains(m)) {
                        hooksCalled.add(m);
                        m.invoke(instance, injector, calledMethod);
                    }
                } else {
                    m.invoke(instance, injector, calledMethod);
                }
            }
        } catch (ReflectiveOperationException ex) {
            throw new HookException("Failed to execute LazyInvocation hook method", ex);
        }
    }

    @Override
    protected void handleInterfacedMethods(InternalInjector injector, Object instance, Class<?> cls) {
        LazyInvocation lazyInvocation = (LazyInvocation) instance;

        if (lazyInvocation.onlyInvokeFirst()) {
            if (!called) {
                called = true;
                lazyInvocation.lazilyInvoked(injector, calledMethod);
            }
        } else {
            lazyInvocation.lazilyInvoked(injector, calledMethod);
        }
    }
}
