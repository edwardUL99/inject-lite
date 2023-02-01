package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.hooks.ConstructedHook;
import io.github.edwardUL99.inject.lite.hooks.PreConstructHook;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BaseHookHandlerTest {
    private BaseHookHandler handler;
    private Class<? extends Hook> supportedHook;

    @BeforeEach
    public void init() {
        supportedHook = PreConstructHook.class;

        handler = spy(new BaseHookHandler() {
            @Override
            protected Class<? extends Hook> getHookType() {
                return supportedHook;
            }

            @Override
            protected void handleInterfacedMethods(InternalInjector injector, Object instance, Class<?> cls) {}
        });
    }

    @Test
    public void testHandle() {
        InternalInjector mockInjector = mock(InternalInjector.class);
        TestNonPreConstruct nonPreConstruct = new TestNonPreConstruct();

        handler.handle(mockInjector, null, TestPreConstruct.class);

        verify(handler).handleInterfacedMethods(mockInjector, null, TestPreConstruct.class);
        reset(handler);

        handler.handle(mockInjector, nonPreConstruct, TestNonPreConstruct.class);

        verify(handler, times(0)).handleInterfacedMethods(mockInjector, nonPreConstruct, TestNonPreConstruct.class);
    }

    private static class TestPreConstruct implements PreConstructHook {}

    private static class TestNonPreConstruct implements ConstructedHook {
        @Override
        public void constructed(Injector injector) {
        }
    }
}