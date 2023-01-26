package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.hooks.PostConstruct;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

/**
 * Handles post construction hooks
 */
public class PostConstructHandler extends BaseHookHandler {
    @Override
    protected Class<? extends Hook> getHookType() {
        return PostConstruct.class;
    }

    @Override
    protected void doHandle(InternalInjector injector, Object instance, Class<?> cls) {
        PostConstruct postConstruct = (PostConstruct) instance;
        postConstruct.postConstruct(injector);
    }
}
