package io.github.edwardUL99.inject.lite.internal.constructors;

import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

public class ConstructorInjectorFactoryTest {
    @Test
    public void testGetConstructorInjector() {
        ConstructorInjector injector = ConstructorInjectorFactory.getConstructorInjector(mock(InternalInjector.class));
        assertInstanceOf(DefaultConstructorInjector.class, injector);
    }
}
