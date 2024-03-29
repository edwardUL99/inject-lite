package io.github.edwardUL99.inject.lite.internal.dependency.graph;

import io.github.edwardUL99.inject.lite.exceptions.CircularDependencyException;
import io.github.edwardUL99.inject.lite.internal.dependency.Dependency;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;

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

    /*
    Checks if the parent and dependency has the same names as this may result in a conflict
    and circular dependency
     */
    private void checkConflictingDependency(Dependency parent, Dependency dependency) {
        Class<?> parentType = parent.getType();
        Class<?> dependencyType = dependency.getType();
        String parentName = parent.getName();
        String dependencyName = dependency.getName();

        if (parentName.equals(dependencyName) &&
                (ReflectionUtils.isAssignable(parentType, dependencyType) || ReflectionUtils.isAssignable(dependencyType, parentType)))
            throw new CircularDependencyException(String.format("Dependency being injected has same name as parent:" +
                    " Parent: %s, Dependency: %s. Try specifying a name of a dependency different to this one to" +
                    " avoid this conflict", parentName, dependencyName));
    }

    /**
     * Add the dependency to the parent class
     * @param parent the parent dependency depending on the dependency
     * @param dependency the dependency
     */
    public void addDependency(Dependency parent, Dependency dependency) {
        checkConflictingDependency(parent, dependency);
        dependencies.computeIfAbsent(parent, k -> new ArrayList<>()).add(dependency);

        checkCircular(parent, dependency, new StringBuilder().append("Dependency: ").append(parent.getName())
                .append(" has a circular dependency: "));
    }
}
