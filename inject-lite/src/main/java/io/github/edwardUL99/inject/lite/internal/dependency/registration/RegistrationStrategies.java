package io.github.edwardUL99.inject.lite.internal.dependency.registration;

import io.github.edwardUL99.inject.lite.annotations.processing.AnnotatedClass;

/**
 * A factory class for registration strategies
 */
public final class RegistrationStrategies {
    private RegistrationStrategies() {}

    /**
     * Creates a strategy for dependencies annotated with the Injectable annotation
     * @param cls the dependency class
     * @return the registration strategy
     */
    public static RegistrationStrategy forInjectable(Class<?> cls) {
        return new InjectableRegistrationStrategy(cls);
    }

    /**
     * Get a strategy for registering a custom annotated class
     * @param annotatedClass the custom annotated class
     * @return the registration strategy
     */
    public static <T> RegistrationStrategy forCustom(AnnotatedClass<T> annotatedClass) {
        return new CustomRegistrationStrategy<>(annotatedClass);
    }
}
