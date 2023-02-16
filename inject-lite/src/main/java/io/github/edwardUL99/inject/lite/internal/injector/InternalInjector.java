package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.exceptions.DependencyMismatchException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.dependency.CommonDependencyHandler;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencyHandlerFactory;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraph;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.dependency.selection.DependencySelection;
import io.github.edwardUL99.inject.lite.internal.dependency.selection.DependencySelectionStrategy;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.methods.MethodInjector;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Supplier;

/**
 * This provides an internal API for the library. Not to be used by clients as it can change
 * any time (methods removed/added without warning)
 */
public interface InternalInjector extends Injector {
    /**
     * Register a dependency that's already created
     * @param dependency the dependency to register
     */
    void registerInjectableDependency(InjectableDependency dependency);

    /**
     * Injects with an already set graph through {@link io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector#setDependencyGraph(DependencyGraph)}
     * @param name the name of the dependency
     * @param expected the expected type
     * @return the injected dependency
     * @param <T> the type of the dependency
     */
    <T> T injectWithGraph(String name, Class<T> expected) throws DependencyNotFoundException, DependencyMismatchException;

    /**
     * Get the injectable dependency with the provided name
     * @param name the name of the dependency
     * @param expected the expected type
     * @return the dependency if found, null otherwise
     * @throws DependencyMismatchException if no match found
     */
    InjectableDependency getInjectableDependency(String name, Class<?> expected) throws DependencyMismatchException;

    /**
     * Get a list of dependencies assignable to the provided type
     * @param type the type of the dependency
     * @return the dependencies if found, otherwise empty list
     */
    List<InjectableDependency> getInjectableDependencies(Class<?> type);

    /**
     * Get a dependency assignable to the provided type using the selector returned by {@link #dependencySelectionStrategy()}
     * @param type the type of the dependency
     * @return the dependency if found, otherwise null
     */
    default InjectableDependency getInjectableDependency(Class<?> type) {
        return getInjectableDependency(type, Configuration.global.isRequireNamedMultipleMatch());
    }

    /**
     * Get an injectable dependency, checking for ambiguity based on configuration
     * @param type the type of the dependency
     * @param checkAmbiguity true to check for ambiguity, false not to
     * @return the dependency if found, otherwise null
     */
    default InjectableDependency getInjectableDependency(Class<?> type, boolean checkAmbiguity) {
        return getInjectableDependency(type, () -> null, checkAmbiguity);
    }

    /**
     * Get a dependency handler for this injector
     * @return the handler instance
     */
    default CommonDependencyHandler getDependencyHandler() {
        return DependencyHandlerFactory.getDependencyHandler(this);
    }

    /**
     * Get an injectable dependency, checking for ambiguity based on configuration. If nameSupplier does not return null,
     * the name will be used
     * @param type the type of the dependency
     * @param nameSupplier a supplier to supply the name of the dependency to search for. If returns null, the
     *                     dependency will be found by the type
     * @param checkAmbiguity true to check for ambiguity, false not to
     * @return the dependency if found, otherwise null
     */
    default InjectableDependency getInjectableDependency(Class<?> type, Supplier<String> nameSupplier, boolean checkAmbiguity) {
        String name = nameSupplier.get();

        if (name != null) return getInjectableDependency(name, type);

        if (checkAmbiguity) {
            return getDependencyHandler().getUnnamedDependency(type);
        } else {
            return dependencySelectionStrategy()
                    .selectDependency(getInjectableDependencies(type));
        }
    }

    /**
     * Injects with an already set graph through {@link io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector#setDependencyGraph(DependencyGraph)}
     * @param type the type of the dependency to inject
     * @param dependency the dependency if already found. Should be retrieved using getInjectableDependency
     * @return the injected dependency
     * @param <T> the type of the dependency
     */
    <T> T injectWithGraph(Class<T> type, InjectableDependency dependency) throws DependencyNotFoundException;

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
     * Get the method injector used to inject methods annotated with Inject
     * @return method injector for this injector
     */
    MethodInjector getMethodInjector();

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
     * Get the strategy used for selecting a dependency from a list
     * @return the strategy
     */
    default DependencySelectionStrategy dependencySelectionStrategy() {
        return Configuration.global.isSelectFirstDependency() ?
                DependencySelection.firstMatchSelector() : DependencySelection.prioritySelector();
    }
}
