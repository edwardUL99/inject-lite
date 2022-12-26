package io.github.edwardUL99.inject.lite;

import io.github.edwardUL99.inject.lite.config.ConfigurationBuilder;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.threads.ExecutorServiceExecutor;
import io.github.edwardUL99.inject.lite.internal.threads.Threads;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;

/**
 * A factory class for getting injectors
 */
public final class Injection {
    /**
     * Indicates if injection has already been configured
     */
    private static boolean configured;
    /**
     * The stack trace where the injection was configured
     */
    private static StackTraceElement configuredStackTrace;

    private Injection() {}

    /**
     * Configure the injection library using the provided builder
     * @param configurationBuilder the builder containing configuration logic
     */
    public static void configure(ConfigurationBuilder configurationBuilder) {
        if (!configured) {
            Configuration.global = configurationBuilder.build();
            configured = true;
            configuredStackTrace = Thread.currentThread().getStackTrace()[2];
        } else {
            throw new InjectionException("Injection has already been configured, you cannot configure it more than once.\n" +
                    "It was previously called at: " + configuredStackTrace);
        }
    }

    /**
     * Set the base packages to search for injectables on the classpath
     * @param injectionPackages the base packages prefix
     * @deprecated Use {@link #configure(ConfigurationBuilder)}. This method simply just calls that
     * method and may be removed in a future release
     */
    @Deprecated
    public static void setInjectionPackages(String...injectionPackages) {
        configure(new ConfigurationBuilder().withInjectionPackagePrefixes(injectionPackages));
    }

    /**
     * Create an injector object and use it to inject objects
     * @return the new injector instance
     */
    public static Injector newInjector() {
        return InjectionContext.createInjector();
    }

    /**
     * Create an injector that is a singleton instance available to the whole application. Global injectors are global
     * on a per-thread basis. I.e. if you call this method in async code, a new global instance will be created and
     * returned for that thread. To use the same injector as the parent thread, use the executor returned by
     * {@link #sharedInjectionExecutor()} to execute async code which uses the same injector
     * @return the singleton global injector
     */
    public synchronized static Injector globalInjector() {
        return InjectionContext.getSingletonInjector();
    }

    /**
     * Resets the global injector
     */
    public static void resetGlobalInjector() {
        InjectionContext.destroySingletonInjector(Threads.getCurrentThread());
    }

    /**
     * If you wish to execute asynchronous code which uses the same injection context as the current
     * parent thread, you can use the executor returned by this method which shares the same global injector with child
     * threads
     * @return executor where it uses the shared injector of the thread calling this method
     */
    public static AsynchronousExecutor sharedInjectionExecutor() {
        return new ExecutorServiceExecutor(Thread.currentThread());
    }
}
