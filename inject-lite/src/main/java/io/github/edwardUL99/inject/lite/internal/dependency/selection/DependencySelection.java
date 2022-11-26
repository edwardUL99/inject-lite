package io.github.edwardUL99.inject.lite.internal.dependency.selection;

/**
 * Factory class for getting dependency selection strategies
 */
public final class DependencySelection {
    /**
     * Get a selector that returns the first dependency from the list
     * @return selector
     */
    public static DependencySelectionStrategy firstMatchSelector() {
        return list -> (list.size() == 0) ? null : list.get(0);
    }

    /**
     * Get a selector that returns a dependency based on a priority
     * @return the selected dependency
     */
    public static DependencySelectionStrategy prioritySelector() {
        return new PrioritySelectionStrategy();
    }
}
