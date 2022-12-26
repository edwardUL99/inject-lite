package io.github.edwardUL99.inject.lite.testing;

import io.github.edwardUL99.inject.lite.internal.dependency.DelayedInjectableDependency;

/**
 * An injection proxy for use in a test context
 */
class TestDelayedInjectableDependency extends DelayedInjectableDependency {
    /**
     * The value to return when get is called
     */
    private final Object value;

    /**
     * Create the test injection proxy
     * @param name the name of the proxy
     * @param value the value to return from the get method
     */
    public TestDelayedInjectableDependency(String name, Object value) {
        super(name, value.getClass(), null);
        this.value = value;
    }

    @Override
    public synchronized Object get() {
        return value;
    }

    @Override
    public boolean isInstantiated() {
        return true;
    }
}
