package io.github.edwardUL99.inject.lite.internal.dependency.scanner;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategies;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategy;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;

/**
 * Scan for @Injectable annotated classes and registers them as dependencies
 */
public class InjectableScanner extends ReflectionsDependencyScanner {
    /**
     * Create an instance with the provided reflections
     * @param reflections the reflections used to scan the classpath
     */
    public InjectableScanner(Reflections reflections) {
        super(reflections);
    }

    /**
     * Create a default injectable scanner
     */
    public InjectableScanner() {
        super();
    }

    @Override
    protected RegistrationStrategy getRegistrationStrategy(Class<?> cls) {
        return RegistrationStrategies.forInjectable(cls);
    }

    @Override
    protected Class<? extends Annotation> getAnnotationToScanFor() {
        return Injectable.class;
    }
}
