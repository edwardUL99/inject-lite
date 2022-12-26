package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

/**
 * This class represents a dependency that can be defined as a constant
 */
public class ConstantInjectableDependency extends BaseInjectableDependency {
    /**
     * The dependency value
     */
    private final Object value;

    /**
     * Create the constant injectable dependency
     * @param name the name of the dependency
     * @param type the type of the dependency
     * @param injector the injector instance
     * @param value the constant value
     */
    public ConstantInjectableDependency(String name, Class<?> type, InternalInjector injector, Object value) {
        super(name, type, injector, false);
        this.value = value;
    }

    @Override
    public Object get() {
        return value;
    }

    @Override
    public ConstantInjectableDependency withDifferentName(String name) {
        return new ConstantInjectableDependency(name, type, injector, value);
    }

    @Override
    public boolean isInstantiated() {
        return false;
    }
}
