package io.github.edwardUL99.inject.lite.internal.constructors;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.dependency.CommonDependencyHandler;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencyHandlerFactory;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraph;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

/**
 * Default implementation of the constructor injector interface
 */
public class DefaultConstructorInjector implements ConstructorInjector {
    /**
     * Injector used to get dependencies
     */
    private final InternalInjector injector;
    /**
     * Handler for dependency functions
     */
    private CommonDependencyHandler dependencyHandler;
    /**
     * The dependency graph in use
     */
    private DependencyGraph graph;

    /**
     * The injector to get dependencies with
     * @param injector dependency injection
     */
    public DefaultConstructorInjector(Injector injector) {
        this.injector = (InternalInjector) injector;
        this.dependencyHandler = DependencyHandlerFactory.getDependencyHandler(this.injector);
    }

    // used to allow injection of mock handlers in testing
    void setDependencyHandler(CommonDependencyHandler dependencyHandler) {
        this.dependencyHandler = dependencyHandler;
    }

    // gets the constructor annotated with Inject
    private Constructor<?> getInjectConstructor(Class<?> cls) {
        Constructor<?> injectConstructor = null;
        Constructor<?>[] constructors = cls.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            Inject inject = constructor.getAnnotation(Inject.class);

            if (inject != null) {
                if (injectConstructor != null)
                    throw new InjectionException("Only one constructor annotated with Inject is allowed");

                if (constructor.getParameters().length == 0)
                    throw new InjectionException("Constructors annotated with Inject must have at least one parameter");

                if (!inject.value().isEmpty())
                    throw new InjectionException("@Inject annotation on constructors must not have a name provided, use" +
                            " @Name on parameters instead");
                else
                    injectConstructor = constructor;
            }
        }

        return injectConstructor;
    }

    // gets the no arg constructor
    private Constructor<?> getNoArg(Class<?> cls) {
        try {
            return cls.getDeclaredConstructor();
        } catch (NoSuchMethodException ex) {
            throw new InjectionException("Without Inject annotation, you must have a no-arg constructor");
        }
    }

    private Object inject(String name, Class<?> cls, Constructor<?> constructor) throws ReflectiveOperationException {
        Parameter[] parameters = constructor.getParameters();
        Object[] instances = dependencyHandler.instantiateParameters(name, cls, graph, parameters);

        return constructor.newInstance(instances);
    }

    @Override
    public Object injectConstructor(String name, Class<?> cls) {
        Constructor<?> inject = getInjectConstructor(cls);

        try {
            if (inject == null) {
                return getNoArg(cls).newInstance();
            } else {
                return inject(name, cls, inject);
            }
        } catch (ReflectiveOperationException ex) {
            throw new InjectionException("Failed to inject constructor", ex);
        }
    }

    @Override
    public void setDependencyGraph(DependencyGraph graph) {
        this.graph = graph;
    }
}
