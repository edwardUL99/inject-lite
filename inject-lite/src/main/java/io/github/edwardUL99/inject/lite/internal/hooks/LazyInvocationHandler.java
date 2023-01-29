package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.hooks.LazyInvocation;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.lang.reflect.Method;

/**
 * A handler for lazy invocation
 */
public class LazyInvocationHandler extends BaseHookHandler {
    private boolean called;
    private Method calledMethod;

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
    protected void doHandle(InternalInjector injector, Object instance, Class<?> cls) {
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
