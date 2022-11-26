package io.github.edwardUL99.inject.lite.internal.dependency.scanner;

import io.github.edwardUL99.inject.lite.injector.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MultipleDependencyScannerTest {
    private List<DependencyScanner> scanners;
    private MultipleDependencyScanner dependencyScanner;

    @BeforeEach
    public void init() {
        scanners = new ArrayList<>();
        scanners.add(mock(DependencyScanner.class));
        scanners.add(mock(DependencyScanner.class));

        dependencyScanner = new MultipleDependencyScanner(scanners);
    }

    @Test
    public void testScanDependencies() {
        Injector injector = mock(Injector.class);
        dependencyScanner.scanDependencies(injector);

        scanners.forEach(s -> verify(s).scanDependencies(injector));
    }
}