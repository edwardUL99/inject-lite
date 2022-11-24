package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.config.Configuration;
import io.github.edwardUL99.inject.lite.exceptions.DependencyMismatchException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencyGraph;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencySelection;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencySelectionStrategy;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;

import java.lang.reflect.Modifier;
import java.util.List;

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
     * Get a list of dependencies assignable to the provided type
     * @param type the type of the dependency
     * @return the dependencies if found, otherwise empty list
     */
    List<D> getInjectableDependencies(Class<?> type);

    /**
     * Get a dependency assignable to the provided type using the selector returned by {@link #dependencySelectionStrategy()}
     * @param type the type of the dependency
     * @return the dependency if found, otherwise null
     */
    default D getInjectableDependency(Class<?> type) {
        return dependencySelectionStrategy()
                .selectDependency(getInjectableDependencies(type));
    }

    /**
     * Injects with an already set graph through {@link io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector#setDependencyGraph(DependencyGraph)}
     * @param type the type of the dependency to inject
     * @param dependency the dependency if already found. Should be retrieved using getInjectableDependency
     * @return the injected dependency
     * @param <T> the type of the dependency
     */
    <T> T injectWithGraph(Class<T> type, D dependency) throws DependencyNotFoundException;

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

    // TODO make sure this all works

    /**
     * Get the strategy used for selecting a dependency from a list
     * @return the strategy
     */
    default DependencySelectionStrategy<D> dependencySelectionStrategy() {
        return Configuration.isSelectFirstDependency() ?
                DependencySelection.firstMatchSelector() : DependencySelection.prioritySelector();
    }
}
