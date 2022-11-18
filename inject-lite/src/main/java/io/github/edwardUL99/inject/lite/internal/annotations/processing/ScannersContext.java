package io.github.edwardUL99.inject.lite.internal.annotations.processing;

import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationScanner;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.threads.Threads;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides factory methods for annotation scanners. An internal context class. AnnotationScanners is the public API
 */
public final class ScannersContext {
    /**
     * A map of singleton scanner instances per thread
     */
    private static final Map<Thread, AnnotationScanner> singletons = new HashMap<>();

    /**
     * Create a new scanner instance with a global injector
     * @return the instance of the scanner
     */
    public static AnnotationScanner createScanner() {
        return createScanner(Injector.get());
    }

    private ScannersContext() {}

    /**
     * Create a new scanner instance with specified injector
     * @return the instance of the scanner
     */
    public static AnnotationScanner createScanner(Injector injector) {
        return new DefaultAnnotationScanner((InternalInjector) injector);
    }

    /**
     * Get a global shared singleton instance of an annotation scanner
     * @return the shared instance of the scanner
     */
    public synchronized static AnnotationScanner getSingletonScanner() {
        return singletons.computeIfAbsent(Threads.getCurrentThread(), v -> createScanner());
    }

    /**
     * Sets the global scanner for the current thread
     * @param scanner the scanner to set
     */
    public synchronized static void setSingletonScanner(AnnotationScanner scanner) {
        singletons.put(Threads.getCurrentThread(), scanner);
    }

    /**
     * Internal method to destroy the global scanner for the current thread
     * @param thread the thread executing
     */
    public synchronized static void destroySingletonScanner(Thread thread) {
        singletons.remove(thread);
    }
}
