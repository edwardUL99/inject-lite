package io.github.edwardUL99.inject.lite.internal.constructors;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Name;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.dependency.Dependency;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencyGraph;
import io.github.edwardUL99.inject.lite.internal.injector.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default implementation of the constructor injector interface
 */
public class DefaultConstructorInjector implements ConstructorInjector {
    /**
     * Injector used to get dependencies
     */
    private final InternalInjector<DelayedInjectableDependency> injector;
    /**
     * The dependency graph in use
     */
    private DependencyGraph graph;

    /**
     * The injector to get dependencies with
     * @param injector dependency injection
     */
    @SuppressWarnings("unchecked")
    public DefaultConstructorInjector(Injector injector) {
        this.injector = (InternalInjector<DelayedInjectableDependency>) injector;
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

    private Object injectAnnotated(Name nameAnnotation, String className, Class<?> cls, Class<?> type) {
        String name = nameAnnotation.value();
        if (graph != null) graph.addDependency(new Dependency(className, cls), new Dependency(name, type));

        return injector.injectWithGraph(name, type);
    }

    private String getDependencyName(List<DelayedInjectableDependency> dependencies) {
        int size = dependencies.size();

        if (size == 0) {
            return "";
        } else if (size == 1) {
            return dependencies.get(0).getName();
        } else {
            return dependencies.stream().map(DelayedInjectableDependency::getName)
                    .collect(Collectors.joining(" | "));
        }
    }

    private Object injectUnknown(String className, Class<?> cls, Class<?> type) {
        List<DelayedInjectableDependency> dependencies = injector.getInjectableDependencies(type);
        String name = getDependencyName(dependencies);
        if (graph != null) graph.addDependency(new Dependency(className, cls),
                new Dependency(name, type));

        Function<List<DelayedInjectableDependency>, DelayedInjectableDependency> selector =
                injector.firstMatchSelector();

        return injector.injectWithGraph(type, selector.apply(dependencies), selector);
    }

    private Object inject(String name, Class<?> cls, Constructor<?> constructor) throws ReflectiveOperationException {
        Parameter[] parameters = constructor.getParameters();
        Object[] instances = new Object[parameters.length];

        for (int i = 0; i < instances.length; i++) {
            Parameter parameter = parameters[i];
            Name nameAnnotation = parameter.getAnnotation(Name.class);
            Class<?> type = parameter.getType();

            if (nameAnnotation != null) {
                instances[i] = injectAnnotated(nameAnnotation, name, cls, type);
            } else {
                instances[i] = injectUnknown(name, cls, type);
            }
        }

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
