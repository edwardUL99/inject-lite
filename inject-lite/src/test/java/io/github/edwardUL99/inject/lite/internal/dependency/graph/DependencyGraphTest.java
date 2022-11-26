package io.github.edwardUL99.inject.lite.internal.dependency.graph;

import io.github.edwardUL99.inject.lite.exceptions.CircularDependencyException;
import io.github.edwardUL99.inject.lite.internal.dependency.Dependency;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DependencyGraphTest {
    @Test
    public void testErrorSameDependency() {
        DependencyGraph graph = new DependencyGraph(new Dependency("name", String.class));
        graph.addDependency(new Dependency("name", String.class), new Dependency("name1", Integer.class));
        assertThrows(CircularDependencyException.class, () ->
                graph.addDependency(new Dependency("name1", Integer.class), new Dependency("name", String.class)));
    }

    @Test
    public void testIndirectCircularDependency() {
        Dependency parent = new Dependency("name", String.class);
        Dependency parentChild = new Dependency("name1", Integer.class);
        Dependency integerChild = new Dependency("name2", Float.class);
        Dependency floatChild = new Dependency("name1", Integer.class);

        DependencyGraph graph = new DependencyGraph(parent);
        graph.addDependency(parent, parentChild);
        graph.addDependency(parentChild, integerChild);

        assertThrows(CircularDependencyException.class, () ->
                graph.addDependency(integerChild, floatChild));
    }

    @Test
    public void testNoCircularDependencyDifferentNames() {
        Dependency parent = new Dependency("name", String.class);
        Dependency parentChild = new Dependency("name1", Integer.class);
        Dependency integerChild = new Dependency("name2", Float.class);
        Dependency floatChild = new Dependency("name2", Integer.class);

        DependencyGraph graph = new DependencyGraph(parent);
        graph.addDependency(parent, parentChild);
        graph.addDependency(parentChild, integerChild);
        graph.addDependency(integerChild, floatChild);
    }
}
