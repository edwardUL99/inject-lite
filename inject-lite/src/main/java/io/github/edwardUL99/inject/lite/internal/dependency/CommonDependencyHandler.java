package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.annotations.Lazy;
import io.github.edwardUL99.inject.lite.annotations.Main;
import io.github.edwardUL99.inject.lite.annotations.Name;
import io.github.edwardUL99.inject.lite.annotations.Optional;
import io.github.edwardUL99.inject.lite.exceptions.AmbiguousDependencyException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraph;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.proxy.InjectionMethod;
import io.github.edwardUL99.inject.lite.internal.proxy.Proxies;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class represents an object that can handle various dependency functionality based on the provided injector
 */
public class CommonDependencyHandler {
    /**
     * The injector used for dependency injection
     */
    protected final InternalInjector injector;
    /**
     * Supplier of a null name
     */
    protected static final Supplier<String> NULL_SUPPLIER = () -> null;

    /**
     * Create an instance using the provided injector
     * @param injector the injector instance
     */
    public CommonDependencyHandler(InternalInjector injector) {
        this.injector = injector;
    }

    /**
     * Get a dependency matching the provided class as an unnamed dependency checking for ambiguity
     * @param cls the type of the dependency
     * @return the matching dependency
     */
    public InjectableDependency getUnnamedDependency(Class<?> cls) {
        List<InjectableDependency> dependencies = injector.getInjectableDependencies(cls);
        int size;

        if (dependencies == null) {
            return null;
        } else if ((size = dependencies.size()) > 1) {
            throw new AmbiguousDependencyException(cls);
        } else {
            return (size == 1) ? dependencies.get(0):null;
        }
    }

    /**
     * Get the injectable dependency with the provided type and name supplier which will be used if the configuration property
     * useParameterNameIfUnnamed is used
     * @param type the parameter type
     * @param nameSupplier the supplier of the name=
     * @return the dependency
     */
    public InjectableDependency getInjectableDependency(Class<?> type, Supplier<String> nameSupplier) {
        boolean useParameterName = Configuration.global.isUseParameterNameIfUnnamed();

        if (!useParameterName)
            nameSupplier = NULL_SUPPLIER;

        InjectableDependency dependency =
                injector.getInjectableDependency(type, nameSupplier, Configuration.global.isRequireNamedMultipleMatch());

        if (useParameterName && dependency == null)
            throw new DependencyNotFoundException(nameSupplier.get());

        return dependency;
    }

    private Object injectAnnotated(DependencyGraph graph, Name nameAnnotation,
                                  String className, Class<?> cls, Class<?> type) {
        String name = nameAnnotation.value();
        addToGraph(graph, className, cls, name, type, () -> injector.getInjectableDependency(name, type));

        return injector.injectWithGraph(name, type);
    }

    // only adds to graph if the dependency needs to be instantiated
    private void addToGraph(DependencyGraph graph, String className, Class<?> cls, String name, Class<?> type,
                            Supplier<InjectableDependency> dependencySupplier) {
        if (graph != null) {
            InjectableDependency injectableDependency = dependencySupplier.get();

            if (injectableDependency == null || !injectableDependency.isInstantiated()) {
                graph.addDependency(new Dependency(className, cls),
                        new Dependency(name, type));
            }
        }
    }

    private Object injectUnnamed(DependencyGraph graph, String className, Class<?> cls, Parameter parameter) {
        Class<?> type = parameter.getType();

        InjectableDependency dependency = getInjectableDependency(
                type, parameter::getName
        );

        String name = (dependency != null) ? dependency.getName() : "";
        addToGraph(graph, className, cls, name, type, () -> injector.getInjectableDependency(type));

        return injector.injectWithGraph(type, dependency);
    }

    private Object injectDependency(Name nameAnnotation, DependencyGraph graph, String name, Class<?> cls, Class<?> type,
                                    Parameter parameter) {
        try {
            if (nameAnnotation != null) {
                return injectAnnotated(graph, nameAnnotation, name, cls, type);
            } else {
                return injectUnnamed(graph, name, cls, parameter);
            }
        } catch (DependencyNotFoundException ex) {
            if (parameter.getAnnotation(Optional.class) != null)
                return ReflectionUtils.getDefaultValue(type);
            else
                throw ex;
        }
    }

    /**
     * Instantiate parameters using injector and graph to instantiate them using dependencies
     * @param name the name of the dependency containing the method
     * @param cls the class of the parent dependency
     * @param graph the dependency graph
     * @param parameters array of parameters
     * @return array of instantiated objects
     */
    public Object[] instantiateParameters(String name, Class<?> cls, DependencyGraph graph, Parameter[] parameters) {
        Object[] instances = new Object[parameters.length];

        for (int i = 0; i < instances.length; i++) {
            Parameter parameter = parameters[i];
            Name nameAnnotation = parameter.getAnnotation(Name.class);
            Class<?> type = parameter.getType();

            instances[i] = getDependencyCheckingLazy(
                    parameter.getAnnotation(Lazy.class),
                    type,
                    () -> injectDependency(nameAnnotation, graph, name, cls, type, parameter),
                    injector
            );
        }

        return instances;
    }

    /**
     * Get a dependency instance checking for the presence of a lazy annotation, using the
     * injection method to inject the object
     * @param lazy the lazy annotation
     * @param type the dependency type
     * @param injectionMethod the method to inject the dependency
     * @param injector the injector instance
     * @return the instantiated dependency or a proxy if lazy
     */
    public Object getDependencyCheckingLazy(Lazy lazy, Class<?> type, InjectionMethod injectionMethod, InternalInjector injector) {
        if (!InjectionContext.isLazyBehaviourDisabled() && Configuration.global.isLazyDependenciesEnabled() && lazy != null) {
            try {
                return Proxies.createInjectionProxy(type, injectionMethod, injector);
            } catch (ReflectiveOperationException | IllegalStateException ex) {
                throw new InjectionException("Failed to create a Lazy dependency", ex);
            }
        } else {
            return injectionMethod.inject();
        }
    }
}
