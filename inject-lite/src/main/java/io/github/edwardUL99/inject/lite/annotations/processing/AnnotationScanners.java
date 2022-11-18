package io.github.edwardUL99.inject.lite.annotations.processing;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.ScannersContext;
import io.github.edwardUL99.inject.lite.internal.threads.ExecutorServiceExecutor;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;

/**
 * Provides factory methods for annotation scanners
 */
public final class AnnotationScanners {
    private AnnotationScanners() {}

    /**
     * Create a new scanner instance with specified injector
     * @return the instance of the scanner
     */
    public static AnnotationScanner newScanner(Injector injector) {
        return ScannersContext.createScanner(injector);
    }

    /**
     * Create a new scanner with a global injector
     * @return the scanner
     */
    public static AnnotationScanner newScanner() {
        return newScanner(Injector.get());
    }

    /**
     * Get a global shared singleton instance of an annotation scanner. Global scanners are on a per-thread basis.
     * A new global one is created for each thread. When running in async code, and you wish to use the same scanner
     * in child scanners, use {@link #sharedScannerExecutor()}
     * @return the shared instance of the scanner
     */
    public synchronized static AnnotationScanner globalScanner() {
        return ScannersContext.getSingletonScanner();
    }

    /**
     * If you wish to execute asynchronous code which uses the same injection context as the current
     * parent thread, you can use the executor returned by this method which shares the same global injector with child
     * threads. Note, if you have called {@link Injection#sharedInjectionExecutor()}, you do not need to call this method
     * from the async called code, and vice versa
     * @return executor where it uses the shared injector of the thread calling this method
     */
    public static AsynchronousExecutor sharedScannerExecutor() {
        return new ExecutorServiceExecutor(Thread.currentThread());
    }
}
