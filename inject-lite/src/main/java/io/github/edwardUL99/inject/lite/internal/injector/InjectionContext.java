package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.dependency.DependencyScanner;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableScanner;
import io.github.edwardUL99.inject.lite.internal.threads.Threads;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal context storing injection contexts. Injection class is the public API to this context.
 */
public final class InjectionContext {
    /**
     * A map of singleton injector instances for each thread
     */
    private static final Map<Thread, Injector> singletons = new HashMap<>();
    /**
     * Used to search for annotated classes
     */
    private static DependencyScanner scanner;
    /**
     * The factory used for creating injectors
     */
    private static InjectorFactory injectorFactory = new DefaultInjectorFactory();
    /**
     * Indicates if the instances created should be singletons on a global level. Overrides singleton instance variable
     */
    private static final Map<Thread, Boolean> singletonBehaviourEnabled = new ConcurrentHashMap<>();

    private InjectionContext() {}

    /**
     * Set the scanner used to search for dependencies
     * @param scanner new scanner to use
     */
    static void setScanner(DependencyScanner scanner) {
        InjectionContext.scanner = scanner;
    }

    /**
     * Set the factory used for creating injectors
     * @param factory the factory instance
     */
    public static void setInjectorFactory(InjectorFactory factory) {
        injectorFactory = factory;
    }

    /**
     * Get a singleton injector for the current thread
     * @return the injector
     */
    public synchronized static Injector getSingletonInjector() {
        return singletons.computeIfAbsent(Threads.getCurrentThread(), v -> createInjector());
    }

    /**
     * Set the singleton injector for the current thread
     * @param injector the injector to set
     */
    public synchronized static void setSingletonInjector(Injector injector) {
        singletons.put(Threads.getCurrentThread(), injector);
    }

    /**
     * Determine if the singleton injector has been set for the thread
     * @return true if set, false if not
     */
    public synchronized static boolean isSingletonSet() {
        return singletons.get(Threads.getCurrentThread()) != null;
    }

    /**
     * Get the scanner, initializing it to InjectableScanner if null
     * @return dependency scanner
     */
    private static DependencyScanner getScanner() {
        if (scanner == null) {
            scanner = new InjectableScanner();
        }

        return scanner;
    }

    /**
     * Create a new injector
     * @return the new injector
     */
    public static Injector createInjector() {
        Injector injector = injectorFactory.create();
        getScanner().scanDependencies(injector);

        return injector;
    }

    /**
     * Destroys the global injector
     * @param thread the thread executing
     */
    public synchronized static void destroySingletonInjector(Thread thread) {
        singletons.remove(thread);
        DelayedInjectableDependency.tearDownInstances(thread);
        singletonBehaviourEnabled.remove(thread);
    }

    /**
     * Get a map of injectors mapped to the thread that owns them
     * @return the map of injectors
     */
    public synchronized static Map<Thread, Injector> getThreadInjectors() {
        return Collections.unmodifiableMap(singletons);
    }

    /**
     * By default, injection proxies use singleton objects. To temporarily disable this, you can call this method
     * with false, one you call it with true again when finished
     * @param singleton true to enable singleton behaviour, false to disable it
     */
    public static void setSingletonBehaviour(boolean singleton) {
        singletonBehaviourEnabled.put(Threads.getCurrentThread(), singleton);
    }

    /**
     * Determines if singleton behaviour is enabled or disabled
     * @return true if global singleton behaviour is enabled/disabled
     */
    public static boolean isSingletonBehaviourEnabled() {
        return singletonBehaviourEnabled.getOrDefault(Threads.getCurrentThread(), true);
    }
}
