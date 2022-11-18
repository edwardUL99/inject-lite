package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.container.ContainersInternal;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategies;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategy;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

/**
 * Scan for @Injectable annotated classes and registers them as dependencies
 */
public class InjectableScanner implements DependencyScanner {
    /**
     * Object used to scan the classpath for annotated classes
     */
    private final Reflections reflections;

    /**
     * Create an instance with the provided reflections
     * @param reflections the reflections used to scan the classpath
     */
    public InjectableScanner(Reflections reflections) {
        this.reflections = reflections;
    }

    /**
     * Create a default injectable scanner
     */
    public InjectableScanner() {
        this(ReflectionUtils.getReflections());
    }

    @Override
    public void scanDependencies(Injector injector) {
        List<Class<?>> classes = new ArrayList<>(reflections.getTypesAnnotatedWith(Injectable.class));

        for (Class<?> cls : classes) {
            RegistrationStrategy registrationStrategy = RegistrationStrategies.forInjectable(cls);
            ContainersInternal.registerDependencyCheckingContainer(injector, registrationStrategy, cls);
        }
    }
}
