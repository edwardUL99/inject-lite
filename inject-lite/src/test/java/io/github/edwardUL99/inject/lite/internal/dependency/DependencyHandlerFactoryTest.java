package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

public class DependencyHandlerFactoryTest {
    @Test
    public void testGetDependencyHandler() {
        assertInstanceOf(CommonDependencyHandlerImproved.class, DependencyHandlerFactory.getDependencyHandler(mock(InternalInjector.class)));
    }
}