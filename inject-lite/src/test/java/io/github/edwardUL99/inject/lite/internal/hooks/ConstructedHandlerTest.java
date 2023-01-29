package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.hooks.Constructed;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ConstructedHandlerTest {
    private ConstructedHandler handler;
    private Constructed postConstruct;

    @BeforeEach
    public void init() {
        handler = new ConstructedHandler();
        postConstruct = spy(new Constructed() {
            @Override
            public void constructed(Injector injector) {

            }
        });
    }

    @Test
    public void testHandle() {
        InternalInjector mockInjector = mock(InternalInjector.class);

        handler.handle(mockInjector, postConstruct, Constructed.class);

        verify(postConstruct).constructed(mockInjector);
    }
}