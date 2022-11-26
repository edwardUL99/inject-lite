package io.github.edwardUL99.inject.lite.internal.dependency.scanner;

import io.github.edwardUL99.inject.lite.injector.Injector;

/**
 * An interface defining an object that scans for dependencies and registers them to the provided injector
 */
public interface DependencyScanner {
    /**
     * Scan for dependencies and register them to the provided injector
     * @param injector the injector to register dependencies to
     */
    void scanDependencies(Injector injector);
}
