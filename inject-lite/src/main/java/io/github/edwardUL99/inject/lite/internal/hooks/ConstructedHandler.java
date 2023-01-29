package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.hooks.Constructed;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

/**
 * Handles post construction hooks
 */
public class ConstructedHandler extends BaseHookHandler {
    @Override
    protected Class<? extends Hook> getHookType() {
        return Constructed.class;
    }

    @Override
    protected void doHandle(InternalInjector injector, Object instance, Class<?> cls) {
        ((Constructed)instance).constructed(injector);
    }
}
