package io.github.edwardUL99.inject.lite.internal.dependency.selection;

import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;

import java.util.List;

/**
 * A strategy for selecting from one or more dependencies when multiple match the same type
 */
public interface DependencySelectionStrategy {
    /**
     * Select the dependency from the provided list
     * @param dependencies the dependencies list
     * @return the selected dependency
     */
    InjectableDependency selectDependency(List<InjectableDependency> dependencies);
}
