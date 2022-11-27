package io.github.edwardUL99.inject.lite.internal.dependency.graph;

import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.dependency.Dependency;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraph;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.util.function.Supplier;

/**
 * A helper class to setup injection in the context of a graph
 */
public final class GraphInjection {
    private GraphInjection() {}

    /**
     * Executes the given supplier returning a dependency in the context of a graph. The supplier could be an example of injecting dependencies
     * using the graph
     * @param injector the injector performing the injection
     * @param name the name of the dependency being injection
     * @param cls the class object being injected
     * @param supplier the supplier of the injected dependency
     * @return the injected value
     * @param <T> the type of the dependency
     */
    public static <T> T executeInGraphContext(InternalInjector injector, String name, Class<T> cls, Supplier<T> supplier) {
        DependencyGraph graph = new DependencyGraph(new Dependency(name, cls));
        ConstructorInjector constructorInjector = injector.getConstructorInjector();
        FieldInjector fieldInjector = injector.getFieldInjector();

        fieldInjector.setDependencyGraph(graph);
        constructorInjector.setDependencyGraph(graph);

        T instance = supplier.get();
        constructorInjector.setDependencyGraph(null);

        return instance;
    }
}
