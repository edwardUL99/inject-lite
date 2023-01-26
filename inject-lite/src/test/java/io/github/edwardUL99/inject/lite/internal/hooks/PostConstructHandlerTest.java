package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.hooks.PostConstruct;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PostConstructHandlerTest {
    private PostConstructHandler handler;
    private PostConstruct postConstruct;

    @BeforeEach
    public void init() {
        handler = new PostConstructHandler();
        postConstruct = spy(new PostConstruct() {
            @Override
            public void postConstruct(Injector injector) {

            }
        });
    }

    @Test
    public void testHandle() {
        InternalInjector mockInjector = mock(InternalInjector.class);

        handler.handle(mockInjector, postConstruct, PostConstruct.class);

        verify(postConstruct).postConstruct(mockInjector);
    }
}