package io.github.edwardUL99.inject.lite.hooks;

import io.github.edwardUL99.inject.lite.internal.hooks.Hook;

/**
 * Since Java does not allow the specification of a static method to be implemented, when this interface is used,
 * the injector will look for a method called <i>preConstruct()</i> with an optional parameter of type
 * {@link io.github.edwardUL99.inject.lite.injector.Injector}
 */
public interface PreConstruct extends Hook {
}
