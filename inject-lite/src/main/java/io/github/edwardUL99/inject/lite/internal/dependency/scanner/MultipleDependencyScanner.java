package io.github.edwardUL99.inject.lite.internal.dependency.scanner;

import io.github.edwardUL99.inject.lite.injector.Injector;

import java.util.List;

/**
 * A scanner composed of multiple dependency scanners
 */
public class MultipleDependencyScanner implements DependencyScanner {
    /**
     * The list of the scanners
     */
    private final List<DependencyScanner> scanners;

    /**
     * Create the scanner delegating to the list of the scanners
     * @param scanners the list of scanners
     */
    public MultipleDependencyScanner(List<DependencyScanner> scanners) {
        this.scanners = scanners;
    }

    @Override
    public void scanDependencies(Injector injector) {
        scanners.forEach(s -> s.scanDependencies(injector));
    }
}
