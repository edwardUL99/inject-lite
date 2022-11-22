package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.exceptions.DependencyMismatchException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencyGraph;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Function;

/**
 * This provides an internal API for the library. Not to be used by clients as it can change
 * any time (methods removed/added without warning)
 * @param <D> the type of injectable dependencies the injector supports
 */
public interface InternalInjector<D extends InjectableDependency> extends Injector {
    /**
     * Register a dependency that's already created
     * @param dependency the dependency to register
     */
    void registerInjectableDependency(D dependency);

    /**
     * Injects with an already set graph through {@link io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector#setDependencyGraph(DependencyGraph)}
     * @param name the name of the dependency
     * @param expected the expected type
     * @return the injected dependency
     * @param <T> the type of the dependency
     */
    <T> T injectWithGraph(String name, Class<T> expected) throws DependencyNotFoundException, DependencyMismatchException;

    /**
     * Get a proxy assignable to the provided type
     * @param type the type of the proxy
     * @return the proxy if found, otherwise null
     */
    List<D> getInjectableDependencies(Class<?> type);

    /**
     * Injects with an already set graph through {@link io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector#setDependencyGraph(DependencyGraph)}
     * @param type the type of the dependency to inject
     * @param dependency the dependency if already found. Should be retrieved using getInjectableDependency
     * @param dependencySelector selects from the list of dependencies the dependency to return
     * @return the injected dependency
     * @param <T> the type of the dependency
     */
    <T> T injectWithGraph(Class<T> type, D dependency, Function<List<D>, D> dependencySelector) throws DependencyNotFoundException;

    /**
     * Get the injector used to inject constructors with
     * @return constructor injector for this injector
     */
    ConstructorInjector getConstructorInjector();

    /**
     * Get the field injector used to inject field annotated values with
     * @return field injector for this injector
     */
    FieldInjector getFieldInjector();

    /**
     * Determines if the provided class can be instantiated by the injector
     * @param cls the class of the injectable
     * @return true if it can be injected, false if not
     */
    default boolean canInject(Class<?> cls) {
        return !Modifier.isAbstract(cls.getModifiers()) && Modifier.isPublic(cls.getModifiers()) && !cls.isEnum() &&
                !cls.isAnnotation() && !cls.isInterface();
    }

    /**
     * Returns a selector for selecting the first match from the list
     * @return the dependency selector
     */
    default Function<List<D>, D> firstMatchSelector() {
        return list -> (list.size() > 0) ? list.get(0) : null;
    }
}
