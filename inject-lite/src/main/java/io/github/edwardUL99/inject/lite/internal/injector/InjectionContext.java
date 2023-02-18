package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.dependency.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.dependency.scanner.ConstantsScanner;
import io.github.edwardUL99.inject.lite.internal.dependency.scanner.DependencyScanner;
import io.github.edwardUL99.inject.lite.internal.dependency.scanner.InjectableScanner;
import io.github.edwardUL99.inject.lite.internal.dependency.scanner.MultipleDependencyScanner;
import io.github.edwardUL99.inject.lite.internal.utils.ThreadAwareValue;

import java.util.Arrays;
import java.util.Map;

/**
 * Internal context storing injection contexts. Injection class is the public API to this context.
 */
public final class InjectionContext {
    /**
     * A map of singleton injector instances for each thread
     */
    private static final ThreadAwareValue<Injector> singletons = new ThreadAwareValue<>(null, true);
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
    private static final ThreadAwareValue<Boolean> singletonBehaviourEnabled = new ThreadAwareValue<>(true, true);
    /**
     * Indicates if lazy behaviour is disabled for this thread
     */
    private static final ThreadAwareValue<Boolean> lazyBehaviourDisabled = new ThreadAwareValue<>(false, true);

    private InjectionContext() {}

    /**
     * Set the scanner used to search for dependencies
     * @param scanner new scanner to use
     */
    public static void setScanner(DependencyScanner scanner) {
        InjectionContext.scanner = scanner;
    }

    /**
     * Get current scanner instance without lazy init
     * @return current scanner instance
     */
    static DependencyScanner getCurrentScanner() {
        return scanner;
    }

    /**
     * Get injector singletons
     * @return injector singletons
     */
    static ThreadAwareValue<Injector> getSingletons() {
        return singletons;
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
        return singletons.getValueOrInsert(InjectionContext::createInjector);
    }

    /**
     * Set the singleton injector for the current thread
     * @param injector the injector to set
     */
    public synchronized static void setSingletonInjector(Injector injector) {
        singletons.setValue(injector);
    }

    /**
     * Determine if the singleton injector has been set for the thread
     * @return true if set, false if not
     */
    public synchronized static boolean isSingletonSet() {
        return singletons.getValue() != null;
    }

    /**
     * Get the scanner, initializing it to InjectableScanner if null
     * @return dependency scanner
     */
    private static DependencyScanner getScanner() {
        if (scanner == null) {
            scanner = new MultipleDependencyScanner(
                    Arrays.asList(
                            new InjectableScanner(),
                            new ConstantsScanner()
                    )
            );
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
        return singletons.getAllValuesAsMap();
    }

    /**
     * By default, injection proxies use singleton objects. To temporarily disable this, you can call this method
     * with false, one you call it with true again when finished
     * @param singleton true to enable singleton behaviour, false to disable it
     */
    public static void setSingletonBehaviour(boolean singleton) {
        singletonBehaviourEnabled.setValue(singleton);
    }

    /**
     * Determines if singleton behaviour is enabled or disabled
     * @return true if global singleton behaviour is enabled/disabled
     */
    public static boolean isSingletonBehaviourEnabled() {
        return singletonBehaviourEnabled.getValue();
    }

    /**
     * By default, injection respects lazy dependencies if enabled. To temporarily disable this, you can call this method
     * with false, one you call it with true again when finished
     * @param disableLazy true to disable lazy behaviour, false to enable it
     */
    public static void setLazyBehaviourDisabled(boolean disableLazy) {
        lazyBehaviourDisabled.setValue(disableLazy);
    }

    /**
     * Determines if lazy behaviour is or disabled or enabled
     * @return true if global singleton behaviour is disabled/enabled
     */
    public static boolean isLazyBehaviourDisabled() {
        return lazyBehaviourDisabled.getValue();
    }
}
