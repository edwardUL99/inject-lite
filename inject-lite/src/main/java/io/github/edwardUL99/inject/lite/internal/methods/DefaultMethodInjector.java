package io.github.edwardUL99.inject.lite.internal.methods;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.dependency.CommonDependencyHandler;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencyHandlerFactory;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraph;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Default implementation of the method injector
 */
public class DefaultMethodInjector implements MethodInjector {
    /**
     * Handler for dependency functions
     */
    private CommonDependencyHandler dependencyHandler;
    /**
     * Graph for dependency injection
     */
    private DependencyGraph graph;

    /**
     * Create an instance with the provided injector
     * @param injector the injector instance
     */
    public DefaultMethodInjector(Injector injector) {
        this.dependencyHandler = DependencyHandlerFactory.getDependencyHandler((InternalInjector) injector);
    }

    // used to allow injection of mock handlers in testing
    void setDependencyHandler(CommonDependencyHandler dependencyHandler) {
        this.dependencyHandler = dependencyHandler;
    }

    private void validateMethod(Method m) {
        int modifiers = m.getModifiers();

        if (!Modifier.isPublic(modifiers) || (Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers)))
            throw new InjectionException("Methods annotated with @Inject must be public, non-abstract and non-static");

        if (!m.getAnnotation(Inject.class).value().isEmpty())
            throw new InjectionException("@Inject annotation on methods must not have a name provided, use" +
                    " @Name on parameters instead");
    }

    private void recurseSearchMethods(Class<?> cls, List<Method> methods) {
        if (cls != null) {
            Arrays.stream(cls.getDeclaredMethods())
                    .filter(m -> m.getAnnotation(Inject.class) != null)
                    .peek(this::validateMethod)
                    .forEach(methods::add);

            if (!Configuration.global.isSingleLevelInjection())
                recurseSearchMethods(cls.getSuperclass(), methods);
        }
    }

    // get inject annotated methods
    private List<Method> getMethods(Class<?> cls) {
        List<Method> methods = new ArrayList<>();
        recurseSearchMethods(cls, methods);

        return methods;
    }

    private void injectMethod(String name, Class<?> cls, Object object, Method method) throws ReflectiveOperationException {
        Parameter[] parameters = method.getParameters();
        Object[] instances = dependencyHandler.instantiateParameters(name, cls, graph, parameters);

        method.invoke(object, instances);
    }
    @Override
    public void injectMethods(String name, Object object) {
        Class<?> cls = object.getClass();
        List<Method> methods = getMethods(cls);

        try {
            for (Method method : methods)
                injectMethod(name, cls, object, method);
        } catch (ReflectiveOperationException ex) {
            throw new InjectionException("Failed to inject methods", ex);
        }
    }

    @Override
    public void setDependencyGraph(DependencyGraph graph) {
        this.graph = graph;
    }
}
