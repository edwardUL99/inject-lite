package io.github.edwardUL99.inject.lite.internal.dependency.scanner;

import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.container.ContainersInternal;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategy;
import io.github.edwardUL99.inject.lite.internal.reflections.Reflections;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * A base scanner using the reflections library to search for dependencies
 */
public abstract class ReflectionsDependencyScanner implements DependencyScanner {
    /**
     * Object used to scan the classpath for annotated classes
     */
    protected final Reflections reflections;

    /**
     * Create an instance with the provided reflections
     * @param reflections the reflections used to scan the classpath
     */
    public ReflectionsDependencyScanner(Reflections reflections) {
        this.reflections = reflections;
    }

    /**
     * Create a default injectable scanner
     */
    public ReflectionsDependencyScanner() {
        this(ReflectionUtils.getReflections());
    }

    /**
     * Get the dependency registration strategy to use for the dependencies
     * @param cls the type of the dependency to register
     * @return the strategy
     */
    protected abstract RegistrationStrategy getRegistrationStrategy(Class<?> cls);

    /**
     * Get the class of the annotation the reflections library will search for
     * @return the annotation to scan for
     */
    protected abstract Class<? extends Annotation> getAnnotationToScanFor();

    @Override
    public final void scanDependencies(Injector injector) {
        List<Class<?>> classes = new ArrayList<>(reflections.getTypesAnnotatedWith(getAnnotationToScanFor()));

        for (Class<?> cls : classes) {
            RegistrationStrategy registrationStrategy = getRegistrationStrategy(cls);
            ContainersInternal.registerDependencyCheckingContainer(injector, registrationStrategy, cls);
        }
    }
}
