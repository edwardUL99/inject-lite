package io.github.edwardUL99.inject.lite.internal.dependency.scanner;

import io.github.edwardUL99.inject.lite.annotations.Constant;
import io.github.edwardUL99.inject.lite.annotations.ConstantDependencies;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.reflections.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConstantsScannerTest {
    private Reflections mockReflections;
    private Injector mockInjector;
    private ConstantsScanner scanner;

    @BeforeEach
    public void init() {
        mockReflections = mock(Reflections.class);
        mockInjector = mock(Injector.class);
        scanner = new ConstantsScanner(mockReflections);
    }

    @Test
    public void testRegistrationConstants() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        classes.add(ConstantsClass.class);

        when(mockReflections.getTypesAnnotatedWith(ConstantDependencies.class))
                .thenReturn(classes);

        scanner.scanDependencies(mockInjector);

        verify(mockInjector).registerConstantDependency("value", String.class, "Hello World");
        verify(mockInjector).registerConstantDependency("number", long.class, 2L);
        verify(mockInjector, times(0)).registerConstantDependency("ignored", Double.class, 2.0);
    }

    @Test
    public void testRegistrationConstantsInvalidFields() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        classes.add(InvalidConstantsClass.class);

        when(mockReflections.getTypesAnnotatedWith(ConstantDependencies.class))
                .thenReturn(classes);

        assertThrows(InjectionException.class, () ->
                scanner.scanDependencies(mockInjector));
    }

    public static class ConstantsClass {
        @Constant
        public static final String value = "Hello World";
        @Constant("number")
        public static final long num = 2L;
        public static final Double ignored = 2.0;
    }

    private static class InvalidConstantsClass {
        @Constant
        private static final String value = "Hello World";
        @Constant("number")
        public final long num = 2L;
        @Constant
        public static Double num1 = 2.0;
    }
}