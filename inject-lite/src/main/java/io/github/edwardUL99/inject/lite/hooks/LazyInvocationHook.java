package io.github.edwardUL99.inject.lite.hooks;

import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.hooks.Hook;

import java.lang.reflect.Method;

/**
 * This method is called if the dependency has been injected as a lazy dependency and a lazy access call was made on it
 */
public interface LazyInvocationHook extends Hook {
    /**
     * Called when a lazy method is intercepted
     * @param injector the injector instance
     * @param method the method invoked
     */
    void lazilyInvoked(Injector injector, Method method);

    /**
     * This method determines if the hook should only be called on the first invocation
     * @return true if hook should only be called on first invocation, false to be called everytime
     */
    default boolean onlyInvokeFirst() {
        return true;
    }
}
