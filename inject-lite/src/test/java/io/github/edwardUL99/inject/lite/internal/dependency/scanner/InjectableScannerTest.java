package io.github.edwardUL99.inject.lite.internal.dependency.scanner;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.container.ContainersInternal;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategy;
import io.github.edwardUL99.inject.lite.internal.reflections.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class InjectableScannerTest {
    private Reflections mockReflections;
    private InjectableScanner scanner;
    private Injector mockInjector;

    @BeforeEach
    public void init() {
        mockReflections = mock(Reflections.class);
        scanner = new InjectableScanner(mockReflections);
        mockInjector = mock(Injector.class);
    }

    @Test
    public void testRegisterDependencies() {
        try (MockedStatic<ContainersInternal> mockedContainers = mockStatic(ContainersInternal.class)) {
            Set<Class<?>> classes = new LinkedHashSet<>();
            classes.add(TestDependency.class);
            classes.add(TestContainerDependency.class);

            when(mockReflections.getTypesAnnotatedWith(Injectable.class))
                    .thenReturn(classes);

            scanner.scanDependencies(mockInjector);

            for (Class<?> cls : classes)
                mockedContainers.verify(() -> ContainersInternal.registerDependencyCheckingContainer(eq(mockInjector),
                        any(RegistrationStrategy.class), eq(cls)));
        }
    }

    @Injectable("test")
    public static class TestDependency { }

    @Injectable("test1")
    public static class TestContainerDependency { }
}
