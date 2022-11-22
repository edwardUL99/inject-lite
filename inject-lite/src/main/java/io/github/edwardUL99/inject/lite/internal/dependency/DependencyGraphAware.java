package io.github.edwardUL99.inject.lite.internal.dependency;

/**
 * Marks a class as being aware of a dependency graph
 */
public interface DependencyGraphAware {
    /**
     * Set the graph on the object that is aware of a dependency graph
     * @param graph the graph being set
     */
    void setDependencyGraph(DependencyGraph graph);
}
