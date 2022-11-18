package io.github.edwardUL99.inject.lite.internal.dependency.registration;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.injector.Injector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InjectableRegistrationStrategyTest {
    @Test
    public void testStrategy() {
        Injector mockInjector = mock(Injector.class);
        InjectableRegistrationStrategy strategy = new InjectableRegistrationStrategy(TestClass.class);

        strategy.register(mockInjector);

        verify(mockInjector).registerDependency("injectable", TestClass.class, true);
    }

    @Test
    public void testStrategyNonInjectableClass() {
        assertThrows(IllegalArgumentException.class, () ->
                new InjectableRegistrationStrategy(String.class));
    }

    @Injectable("injectable")
    private static class TestClass {}
}
