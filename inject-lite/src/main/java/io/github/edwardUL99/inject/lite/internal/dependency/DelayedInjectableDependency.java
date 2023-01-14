package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.utils.ThreadAwareValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a class that stores a dependency that must be injected and is only instantiated when it is required.
 */
public class DelayedInjectableDependency extends BaseInjectableDependency {
    /**
     * Holds instances associated with the class type
     */
    private static ThreadAwareValue<Map<Dependency, Object>> instances = new ThreadAwareValue<>(new HashMap<>(), true);

    /**
     * Instantiate the dependency
     * @param name the name of the object being proxied
     * @param type the type of the injectable object
     * @param injector the injector for injecting dependencies
     */
    public DelayedInjectableDependency(String name, Class<?> type, InternalInjector injector) {
        super(name, type, injector, true);
    }

    /**
     * Instantiate the dependency
     * @param name the name of the object being proxied
     * @param type the type of the injectable object
     * @param injector the injector for injecting dependencies
     * @param singleton determines if this dependency is a singleton or not (only used when singleton behaviour is enabled)
     */
    public DelayedInjectableDependency(String name, Class<?> type, InternalInjector injector, boolean singleton) {
        super(name, type, injector, singleton);
    }

    /**
     * Set the singleton instances values. Intended for testing
     * @param instances map of threads to singleton instances
     */
    public static void setInstances(ThreadAwareValue<Map<Dependency, Object>> instances) {
        DelayedInjectableDependency.instances = instances;
    }

    /**
     * Determines if the injection should occur on a singleton basis. If singleton behaviour is disabled, it always
     * returns false, otherwise it checks the value of the singleton instance variable
     * @return true if singleton, false if not
     */
    private synchronized boolean isSingletonEnabled() {
        return InjectionContext.isSingletonBehaviourEnabled() && singleton;
    }

    private Object createInstance(Map<Dependency, Object> instances, Dependency dependency) {
        Object instance = injector.getConstructorInjector().injectConstructor(name, type);
        injector.getFieldInjector().injectFields(instance);
        injector.getMethodInjector().injectMethods(name, instance);

        if (instances != null && !instances.containsKey(dependency)) {
            instances.put(dependency, instance);
        }

        return instance;
    }

   @Override
    public synchronized Object get() {
        Dependency dependency = new Dependency(name, type);
        Object instance;
        Map<Dependency, Object> instances = null;

        if (isSingletonEnabled()) {
            instances = DelayedInjectableDependency.instances.getValueOrInsert(HashMap::new);
            instance = instances.get(dependency);
        } else {
            instance = null;
        }

        return (instance == null) ? createInstance(instances, dependency) : instance;
    }

    @Override
    public boolean isInstantiated() {
        return isSingletonEnabled() && instances.getValue().get(new Dependency(name, type)) != null;
    }

    @Override
    public DelayedInjectableDependency withDifferentName(String name) {
        return new DelayedInjectableDependency(name, type, injector, singleton);
    }

    /**
     * Tears down instantiated objects for the current thread. Recommended to be only used internally by the library
     * @param thread the thread executing
     */
    public synchronized static void tearDownInstances(Thread thread) {
        instances.remove(thread);
    }

    /**
     * Factory for creating dependencies of this type
     */
    public static class Factory implements InjectableDependencyFactory {
        @Override
        public DelayedInjectableDependency instantiate(String name, Class<?> type, InternalInjector injector,
                                                       boolean singleton) {
            return new DelayedInjectableDependency(name, type, injector, singleton);
        }
    }
}
