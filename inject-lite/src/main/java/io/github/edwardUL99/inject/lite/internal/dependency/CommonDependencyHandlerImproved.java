package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.annotations.Principal;
import io.github.edwardUL99.inject.lite.exceptions.AmbiguousDependencyException;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides improved dependency handling to support new features to keep the existing CommonDependencyHandler
 * as closed as possible
 */
public class CommonDependencyHandlerImproved extends CommonDependencyHandler {
    /**
     * Create an instance using the provided injector
     *
     * @param injector the injector instance
     */
    public CommonDependencyHandlerImproved(InternalInjector injector) {
        super(injector);
    }

    private InjectableDependency findDependencyCheckingAmbiguity(Class<?> cls, List<InjectableDependency> dependencies) {
        int size = dependencies.size();

        if (size > 1) {
            List<InjectableDependency> principalDependencies = dependencies.stream()
                    .filter(d -> d.getType().getAnnotation(Principal.class) != null)
                    .collect(Collectors.toList());

            if (principalDependencies.size() != 1)
                throw new AmbiguousDependencyException(cls);

            return principalDependencies.get(0);
        }

        return (size == 1) ? dependencies.get(0):null;
    }

    @Override
    public InjectableDependency getUnnamedDependency(Class<?> cls) {
        List<InjectableDependency> dependencies = injector.getInjectableDependencies(cls);

        return (dependencies != null) ? findDependencyCheckingAmbiguity(cls, dependencies) : null;
    }
}
