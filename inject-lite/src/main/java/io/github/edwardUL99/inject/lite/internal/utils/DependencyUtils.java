package io.github.edwardUL99.inject.lite.internal.utils;

import io.github.edwardUL99.inject.lite.exceptions.AmbiguousDependencyException;
import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.injector.InjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.util.List;

/**
 * Provides dependency related utilities
 */
public class DependencyUtils {
    /**
     * Get a dependency matching the provided class as an unnamed dependency checking for ambiguity
     * @param cls the type of the dependency
     * @param injector the injector being used
     * @return the matching dependency
     */
    public static <D extends InjectableDependency> D getUnnamedDependency(Class<?> cls, InternalInjector<D> injector) {
        if (Configuration.global.isRequireNamedMultipleMatch()) {
            List<D> dependencies = injector.getInjectableDependencies(cls);

            if (dependencies == null) {
                return null;
            } else if (dependencies.size() > 1) {
                throw new AmbiguousDependencyException(cls);
            } else {
                return dependencies.get(0);
            }
        } else {
            // rely on selection strategy since ambiguity is allowed
            return injector.getInjectableDependency(cls);
        }
    }
}
