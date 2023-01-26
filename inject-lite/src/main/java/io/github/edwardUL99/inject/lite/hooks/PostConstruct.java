package io.github.edwardUL99.inject.lite.hooks;

import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.hooks.Hook;

/**
 * Represents a hook that is called post construction. It has two methods, one which takes a
 */
public interface PostConstruct extends Hook {
    /**
     * Called after the instance is created
     * @param injector the injector instance that created the instance.
     */
    void postConstruct(Injector injector);
}
