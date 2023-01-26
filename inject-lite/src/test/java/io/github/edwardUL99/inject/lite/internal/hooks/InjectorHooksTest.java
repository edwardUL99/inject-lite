package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.internal.dependency.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.DefaultInjector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class InjectorHooksTest {
    private InjectorHooks.HookSupport hookSupport;
    private HookHandler mockPreConstruct;
    private HookHandler mockPostConstruct;

    @BeforeEach
    public void init() {
        // used for testing
        hookSupport = new DefaultInjector(DelayedInjectableDependency::new);
        mockPreConstruct = mock(HookHandler.class);
        mockPostConstruct = mock(HookHandler.class);

        InjectorHooks.setPreConstruct(mockPreConstruct);
        InjectorHooks.setPostConstruct(mockPostConstruct);
    }

    @AfterAll
    public static void teardown() {
        InjectorHooks.setup();
    }

    @Test
    public void testHookSupportDoPreConstruct() {
        try (MockedStatic<InjectorHooks> injectorHooks = mockStatic(InjectorHooks.class)) {
            hookSupport.doPreConstruct(String.class);
            injectorHooks.verify(() -> InjectorHooks.handlePreConstruct(hookSupport, String.class));
        }
    }

    @Test
    public void testHookSupportDoPostConstruct() {
        try (MockedStatic<InjectorHooks> injectorHooks = mockStatic(InjectorHooks.class)) {
            String s = "Hello";
            hookSupport.doPostConstruct(s, String.class);
            injectorHooks.verify(() -> InjectorHooks.handlePostConstruct(hookSupport, s, String.class));
        }
    }

    @Test
    public void testHandlePreConstruct() {
        InjectorHooks.handlePreConstruct(hookSupport, String.class);
        verify(mockPreConstruct).handle(hookSupport, null, String.class);
        verifyNoInteractions(mockPostConstruct);
    }

    @Test
    public void testHandlePostConstruct() {
        String s = "Hello";
        InjectorHooks.handlePostConstruct(hookSupport, s, String.class);
        verify(mockPostConstruct).handle(hookSupport, s, String.class);
        verifyNoInteractions(mockPreConstruct);
    }
}