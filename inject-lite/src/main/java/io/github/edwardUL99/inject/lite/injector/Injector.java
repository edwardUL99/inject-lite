package io.github.edwardUL99.inject.lite.injector;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.exceptions.DependencyExistsException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyMismatchException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.exceptions.InvalidInjectableException;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;

import java.util.Map;
import java.util.function.Consumer;

/**
 * This object represents an injection context that allows the registration of
 * dependencies and also the injection of them.
 *
 * Do not provide implementations of this interface yourself as they may not be compatible with
 * the functionality provided by the library. The library cannot support custom implementations and
 * is not responsible for any issues arising from it
 */
public interface Injector {
    /**
     * Register the class of the object against the name
     * @param name the name of the dependency
     * @param cls the class of the dependency
     * @param singleton determines if the dependency is a singleton or not
     * @param <T> the type of the dependency
     * @throws DependencyExistsException if a dependency already exists with the given name
     * @throws InvalidInjectableException if the dependency cannot be instantiated and is therefore an invalid injectable
     */
    <T> void registerDependency(String name, Class<T> cls, boolean singleton) throws DependencyExistsException, InvalidInjectableException;

    /**
     * Register a dependency that is a constant value
     * @param name the name of the dependency
     * @param type the type of the constant
     * @param value the constant value
     */
    void registerConstantDependency(String name, Class<?> type, Object value) throws DependencyExistsException;

    /**
     * Inject an object of the given name. Checks that the registered class is the same class as expected or a subclass
     * of it
     * @param name the name of the dependency
     * @param expected the expected class of the registered dependency
     * @return the instantiated dependency
     * @param <T> the type of the dependency
     * @throws DependencyNotFoundException if the dependency cannot be found
     * @throws DependencyMismatchException if the expected type is not the same class or a superclass of the registered
     * dependency.
     */
    <T> T inject(String name, Class<T> expected) throws DependencyNotFoundException, DependencyMismatchException;

    /**
     * Find the first matching dependency that has the same class as the provided type or is a subclass of the provided type.
     * This returns the first dependency that is the same type or a subtype of the provided type. To get a list of all
     * matching dependencies, use {@link #injectAll(Class)}
     * @param type the type of the dependency to find
     * @return the instantiated dependency
     * @param <T> the type of the dependency
     * @throws DependencyNotFoundException if the dependency cannot be found
     */
    <T> T inject(Class<T> type) throws DependencyNotFoundException;

    /**
     * Retrieve a list of all dependencies that are either the same type or a subtype of the provided type
     * @param type the type of the dependency to find
     * @return the map of matching dependencies, mapped with dependency name to dependency
     * @param <T> the type of the dependencies
     * @throws DependencyNotFoundException if 0 dependencies match the type
     */
    <T> Map<String, T> injectAll(Class<T> type) throws DependencyNotFoundException;

        /**
         * Act on dependencies that are either type of subtypes of type with the provided consumer
         * @param consumer the consumer to act on each dependency
         * @param type the type of the dependencies
         * @param <T> the typeof the dependencies to act on
         */
            <T>

    void actOnDependencies(Consumer<T> consumer, Class<T> type);

    /**
     * Act on all dependencies (calls {@link #actOnDependencies(Consumer, Class)} with Object.class
     * @param consumer the consumer to act on the dependencies
     */
    void actOnDependencies(Consumer<Object> consumer);

    /**
     * Instantiate an object of the given type, injecting the constructor and resource annotated fields. Objects
     * created this way are not singletons. Dependencies instantiated this way are not created lazily even if annotated with Lazy
     * @param type the class of the object
     * @return the instantiated object
     * @param <T> the type of the object
     * @throws InjectionException if any injection fails
     */
    <T> T instantiate(Class<T> type) throws InjectionException;

    /**
     * Retrieves an executor that executes all child threads which have access to the same injector as this injector.
     * Default implementation is a wrapper around the Injection factory methods for getting the shared executors
     * @return the executor with injection shared with all child threads
     */
    default AsynchronousExecutor sharedInjectionExecutor() {
        return Injection.sharedInjectionExecutor();
    }

    /**
     * Get a global instance of the injector
     * @return the injector global instance
     */
    static Injector get() {
        return Injection.globalInjector();
    }
}
