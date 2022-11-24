package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.internal.injector.InjectableDependency;

/**
 * Factory class for getting dependency selection strategies
 */
public final class DependencySelection {
    /**
     * Get a selector that returns the first dependency from the list
     * @return selector
     * @param <D> the type of dependency
     */
    public static <D extends InjectableDependency> DependencySelectionStrategy<D> firstMatchSelector() {
        return list -> (list.size() == 0) ? null : list.get(0);
    }

    /**
     * Get a selector that returns a dependency based on a priority
     * @return the selected dependency
     * @param <D> the type of dependency
     */
    public static <D extends InjectableDependency> DependencySelectionStrategy<D> prioritySelector() {
        return new PrioritySelectionStrategy<>();
    }
}
