package io.github.edwardUL99.inject.lite.internal.dependency.scanner;

import io.github.edwardUL99.inject.lite.annotations.ConstantDependencies;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.ConstantsRegistrationStrategy;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategy;
import io.github.edwardUL99.inject.lite.internal.reflections.Reflections;

import java.lang.annotation.Annotation;

/**
 * Scans for constant dependencies
 */
public class ConstantsScanner extends ReflectionsDependencyScanner {
    /**
     * Create an instance with the provided reflections
     * @param reflections the reflections used to scan the classpath
     */
    public ConstantsScanner(Reflections reflections) {
        super(reflections);
    }

    /**
     * Create a default scanner
     */
    public ConstantsScanner() {
        super();
    }

    @Override
    protected RegistrationStrategy getRegistrationStrategy(Class<?> cls) {
        return new ConstantsRegistrationStrategy(cls);
    }

    @Override
    protected Class<? extends Annotation> getAnnotationToScanFor() {
        return ConstantDependencies.class;
    }
}
