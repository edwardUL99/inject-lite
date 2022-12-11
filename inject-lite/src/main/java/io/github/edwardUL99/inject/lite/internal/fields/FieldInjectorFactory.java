package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.injector.Injector;

/**
 * A factory for producing field injectors
 */
public final class FieldInjectorFactory {
    private FieldInjectorFactory() {}

    /**
     * Get a field injector based on a system property being defined
     * @param injector the injector to use for dependencies
     * @return the field injector to use
     */
    public static FieldInjector getFieldInjector(Injector injector) {
        return new DefaultFieldInjector(injector);
    }

}
