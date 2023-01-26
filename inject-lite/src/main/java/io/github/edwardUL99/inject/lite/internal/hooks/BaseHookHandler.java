package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;

/**
 * An abstract hook handler to commonalise code
 */
public abstract class BaseHookHandler implements HookHandler {
    @Override
    public final void handle(InternalInjector injector, Object instance, Class<?> cls) {
        if (ReflectionUtils.isAssignable(getHookType(), cls)) {
            doHandle(injector, instance, cls);
        }
    }

    /**
     * Get the hook this handler is meant to handle
     * @return the hook type
     */
    protected abstract Class<? extends Hook> getHookType();

    /**
     * Called if the class is of the supported hook
     * @param injector the injector
     * @param instance the object instance, can be null
     * @param cls the class type
     */
    protected abstract void doHandle(InternalInjector injector, Object instance, Class<?> cls);
}
