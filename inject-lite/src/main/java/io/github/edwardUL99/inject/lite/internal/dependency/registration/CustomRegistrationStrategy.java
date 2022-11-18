package io.github.edwardUL99.inject.lite.internal.dependency.registration;

import io.github.edwardUL99.inject.lite.annotations.processing.AnnotatedClass;
import io.github.edwardUL99.inject.lite.exceptions.InvalidInjectableException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.InternalAnnotatedClass;
import io.github.edwardUL99.inject.lite.internal.injector.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

/**
 * A strategy for registering custom dependencies
 * @param <T> the custom annotation type
 */
public class CustomRegistrationStrategy<T> implements RegistrationStrategy {
    /**
     * The annotated class
     */
    private final InternalAnnotatedClass<T> annotatedClass;

    /**
     * Create a strategy instance
     * @param annotatedClass the annotated class
     */
    public CustomRegistrationStrategy(AnnotatedClass<T> annotatedClass) {
        if (!(annotatedClass instanceof InternalAnnotatedClass<?>))
            throw new IllegalStateException("Own extensions of AnnotatedClass objects is not supported");

        this.annotatedClass = (InternalAnnotatedClass<T>) annotatedClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(Injector injector) {
        if (!(injector instanceof InternalInjector))
            throw new IllegalStateException("Only internally implemented injectors are supported");

        InternalInjector<DelayedInjectableDependency> internalInjector = (InternalInjector<DelayedInjectableDependency>) injector;
        Class<?> type = annotatedClass.getAnnotatedClass();

        if (!internalInjector.canInject(type))
            throw new InvalidInjectableException(type);

        internalInjector.registerInjectableDependency((DelayedInjectableDependency) annotatedClass.getInjectableDependency());
    }
}
