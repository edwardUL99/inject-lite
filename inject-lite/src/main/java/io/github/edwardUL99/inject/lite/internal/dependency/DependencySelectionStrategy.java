package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.internal.injector.InjectableDependency;

import java.util.List;

/**
 * A strategy for selecting from one or more dependencies when multiple match the same type
 */
public interface DependencySelectionStrategy<D extends InjectableDependency> {
    /**
     * Select the dependency from the provided list
     * @param dependencies the dependencies list
     * @return the selected dependency
     */
    D selectDependency(List<D> dependencies);
}
