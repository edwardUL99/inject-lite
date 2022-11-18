package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.internal.dependency.Dependency;
import io.github.edwardUL99.inject.lite.internal.threads.Threads;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a class that stores a dependency that must be injected and is only instantiated when it is required.
 */
public class DelayedInjectableDependency implements InjectableDependency {
    /**
     * The name of the dependency
     */
    private final String name;
    /**
     * The type of the object
     */
    private final Class<?> type;
    /**
     * The injector implementation
     */
    private final InternalInjector<DelayedInjectableDependency> injector;
    /**
     * Indicates that this proxy should be created as a singleton
     */
    private final boolean singleton;
    /**
     * Holds instances associated with the class type
     */
    private static final Map<Thread, Map<Dependency, Object>> instances = new ConcurrentHashMap<>();

    /**
     * Instantiate the dependency
     * @param name the name of the object being proxied
     * @param type the type of the injectable object
     * @param injector the injector for injecting dependencies
     */
    public DelayedInjectableDependency(String name, Class<?> type, InternalInjector<DelayedInjectableDependency> injector) {
        this(name, type, injector, true);
    }

    /**
     * Instantiate the dependency
     * @param name the name of the object being proxied
     * @param type the type of the injectable object
     * @param injector the injector for injecting dependencies
     * @param singleton determines if this dependency is a singleton or not (only used when singleton behaviour is enabled)
     */
    public DelayedInjectableDependency(String name, Class<?> type, InternalInjector<DelayedInjectableDependency> injector, boolean singleton) {
        this.name = name;
        this.type = type;
        this.injector = injector;
        this.singleton = singleton;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Determines if the injection should occur on a singleton basis. If singleton behaviour is disabled, it always
     * returns false, otherwise it checks the value of the singleton instance variable
     * @return true if singleton, false if not
     */
    private synchronized boolean isSingletonEnabled() {
        return InjectionContext.isSingletonBehaviourEnabled() && singleton;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

   @Override
    public synchronized Object get() {
        // TODO test this singleton code and check other TODOs too
        Dependency dependency = new Dependency(name, type);
        Object instance;
        Map<Dependency, Object> instances = null;

        if (isSingletonEnabled()) {
            instances = DelayedInjectableDependency.instances.computeIfAbsent(Threads.getCurrentThread(),
                    v -> new HashMap<>());
            instance = instances.get(dependency);
        } else {
            instance = null;
        }

        if (instance == null) {
            instance = injector.getConstructorInjector().injectConstructor(name, type);
            injector.getFieldInjector().injectFields(instance);

            if (instances != null && !instances.containsKey(dependency)) {
                instances.put(dependency, instance);
            }
        }

        return instance;
    }

    @Override
    public Class<?> getType() {
        return type;
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
    public static class Factory implements InjectableDependencyFactory<DelayedInjectableDependency> {
        @Override
        public DelayedInjectableDependency instantiate(String name, Class<?> type, InternalInjector<DelayedInjectableDependency> injector,
                                                       boolean singleton) {
            return new DelayedInjectableDependency(name, type, injector, singleton);
        }
    }
}
