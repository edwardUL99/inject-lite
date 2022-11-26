package io.github.edwardUL99.inject.lite.internal.dependency.graph;

import io.github.edwardUL99.inject.lite.exceptions.CircularDependencyException;
import io.github.edwardUL99.inject.lite.internal.dependency.Dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a graph of dependencies. Allows for creating a graph
 * of dependencies which a class depends on
 */
public class DependencyGraph {
    /**
     * The dependent class which requires the dependencies
     */
    private final Dependency dependent;
    /**
     * Mapping of class dependencies
     */
    private final Map<Dependency, List<Dependency>> dependencies;

    /**
     * Create the graph for the dependent class
     * @param dependent the class that depends on dependencies
     */
    public DependencyGraph(Dependency dependent) {
        this.dependent = dependent;
        this.dependencies = new HashMap<>();
    }

    /**
     * Get the dependent class
     * @return the class depending on the dependencies
     */
    public Dependency getDependent() {
        return dependent;
    }

    private void throwCircular(String message) {
        throw new CircularDependencyException(message);
    }

    // TODO improve the naming chains if possible
    private void checkCircular(Dependency dependency, Dependency current, StringBuilder builder) {
        List<Dependency> dependencies = this.dependencies.getOrDefault(current, new ArrayList<>());

        for (Dependency d : dependencies) {
            if (d.isSameDependency(dependency)) {
                builder.append(current.getName()).append("->").append(dependency.getName());
                throwCircular(builder.toString());
            } else {
                builder.append(current.getName()).append("->");
                checkCircular(dependency, d, builder);
            }
        }
    }

    /**
     * Add the dependency to the parent class
     * @param parent the parent dependency depending on the dependency
     * @param dependency the dependency
     */
    public void addDependency(Dependency parent, Dependency dependency) {
        dependencies.computeIfAbsent(parent, k -> new ArrayList<>()).add(dependency);

        checkCircular(parent, dependency, new StringBuilder().append("Dependency: ").append(parent.getName())
                .append(" has a circular dependency: "));
    }
}
