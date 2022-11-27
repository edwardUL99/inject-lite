package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.exceptions.AmbiguousDependencyException;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.util.List;

/**
 * Provides dependency related utilities
 */
public class CommonDependencyFunctions {
    /**
     * Get a dependency matching the provided class as an unnamed dependency checking for ambiguity
     * @param cls the type of the dependency
     * @param injector the injector being used
     * @return the matching dependency
     */
    public static InjectableDependency getUnnamedDependency(Class<?> cls, InternalInjector injector) {
        List<InjectableDependency> dependencies = injector.getInjectableDependencies(cls);
        int size;

        if (dependencies == null) {
            return null;
        } else if ((size = dependencies.size()) > 1) {
            throw new AmbiguousDependencyException(cls);
        } else {
            return (size == 1) ? dependencies.get(0):null;
        }
    }
}
