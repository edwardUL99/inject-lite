package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleLevelFieldInjectorTest {
    private SingleLevelFieldInjector fieldInjector;
    private InternalInjector<?> mockInjector;

    @BeforeEach
    public void init() {
        mockInjector = mock(InternalInjector.class);
        fieldInjector = new SingleLevelFieldInjector(mockInjector);

        when(mockInjector.firstMatchSelector())
                .thenCallRealMethod();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInjectFields() {
        when(mockInjector.injectWithGraph(eq(TestResourceOne.class), eq(null), any(Function.class)))
                .thenReturn(new TestResourceOne());
        ClientChild child = new ClientChild();

        assertNull(child.testResource);
        assertNull(child.testResourceOne);

        fieldInjector.injectFields(child);

        // single level should not have injected parent
        assertNull(child.testResource);
        assertNotNull(child.testResourceOne);
    }
}
