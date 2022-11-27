package io.github.edwardUL99.inject.lite.internal.dependency.selection;

import io.github.edwardUL99.inject.lite.annotations.Priority;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;

import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * A selection strategy based on dependency priority
 */
public class PrioritySelectionStrategy implements DependencySelectionStrategy {
    @Override
    public InjectableDependency selectDependency(List<InjectableDependency> dependencies) {
        int size = dependencies.size();

        if (size == 0) {
            return null;
        } else {
            PriorityQueue<PrioritisedDependency> queue = new PriorityQueue<>();
            queue.addAll(dependencies
                    .stream()
                    .map(PrioritisedDependency::new)
                    .collect(Collectors.toList()));
            PrioritisedDependency dependency = queue.poll();

            return (dependency != null) ? dependency.dependency : null;
        }
    }

    /**
     * The prioritised dependency
     */
    private class PrioritisedDependency implements Comparable<PrioritisedDependency> {
        /**
         * The dependency being prioritised
         */
        private final InjectableDependency dependency;
        /**
         * The priority assigned to the dependency
         */
        private final int priority;

        /**
         * Create the dependency
         * @param dependency dependency being prioritised
         */
        public PrioritisedDependency(InjectableDependency dependency) {
            this.dependency = dependency;
            int priority = Integer.MAX_VALUE;
            Priority annotation = dependency.getType().getAnnotation(Priority.class);

            if (annotation != null) priority = annotation.value();

            this.priority = priority;
        }

        @Override
        public int compareTo(PrioritisedDependency o) {
            return priority - o.priority;
        }
    }
}
