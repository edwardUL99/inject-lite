package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.injector.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MultiLevelFieldInjectorTest {
    private MultiLevelFieldInjector fieldInjector;
    private Injector mockInjector;

    @BeforeEach
    public void init() {
        mockInjector = mock(Injector.class);
        fieldInjector = new MultiLevelFieldInjector(mockInjector);
    }

    @Test
    public void testInjectFields() {
        when(mockInjector.inject("testResource", TestResource.class))
                .thenReturn(new TestResource());
        when(mockInjector.inject(TestResourceOne.class))
                .thenReturn(new TestResourceOne());
        ClientChild child = new ClientChild();

        assertNull(child.testResource);
        assertNull(child.testResourceOne);

        fieldInjector.injectFields(child);

        // multi level should have injected parent
        assertNotNull(child.testResource);
        assertNotNull(child.testResourceOne);
    }
}
