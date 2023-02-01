package io.github.edwardUL99.inject.lite.hooks;

import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.hooks.Hook;

/**
 * Represents a hook that is called post construction
 */
public interface Constructed extends Hook {
    /**
     * Called after the instance is created
     * @param injector the injector instance that created the instance.
     */
    void constructed(Injector injector);
}
