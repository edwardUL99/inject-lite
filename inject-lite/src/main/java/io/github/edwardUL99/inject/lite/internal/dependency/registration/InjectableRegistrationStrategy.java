package io.github.edwardUL99.inject.lite.internal.dependency.registration;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.injector.Injector;

/**
 * A strategy for registering dependencies annotated with Injectable
 */
public class InjectableRegistrationStrategy implements RegistrationStrategy {
    /**
     * The injectable annotation
     */
    private final Injectable injectable;
    /**
     * The class object of the dependency
     */
    private final Class<?> cls;

    /**
     * Create the strategy
     * @param cls the class of the dependency. If not annotated with Injectable, IllegalArgumentException is thrown
     */
    public InjectableRegistrationStrategy(Class<?> cls) {
        this.injectable = cls.getAnnotation(Injectable.class);

        if (this.injectable == null)
            throw new IllegalArgumentException("Class " + cls + " is not annotated with Injectable");

        this.cls = cls;
    }

    @Override
    public void register(Injector injector) {
        injector.registerDependency(injectable.value(), cls, injectable.singleton());
    }
}
