package io.github.edwardUL99.inject.lite.internal.methods;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.internal.dependency.CommonDependencyHandler;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraph;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultMethodInjectorTest {
    private DefaultMethodInjector methodInjector;
    private InternalInjector mockInjector;
    private DependencyGraph mockGraph = mock(DependencyGraph.class);
    private CommonDependencyHandler mockHandler;

    @BeforeEach
    public void init() {
        mockInjector = mock(InternalInjector.class);
        methodInjector = new DefaultMethodInjector(mockInjector);
        mockGraph = mock(DependencyGraph.class);
        methodInjector.setDependencyGraph(mockGraph);
        mockHandler = mock(CommonDependencyHandler.class);
        methodInjector.setDependencyHandler(mockHandler);
    }

    @Test
    public void testSuccessfulInject() throws ReflectiveOperationException {
        String val = "Hello World";
        when(mockHandler.instantiateParameters("test", TestDependency.class, mockGraph,
                        TestDependency.class.getDeclaredMethod("setDependency", String.class).getParameters()))
                .thenReturn(new Object[]{val});

        TestDependency object = new TestDependency();

        assertNull(object.dependency);
        assertFalse(object.nonMethodCalled);

        methodInjector.injectMethods("test", object);

        assertEquals("Hello World", object.dependency);
        assertFalse(object.nonMethodCalled);
        verify(mockHandler).instantiateParameters("test", TestDependency.class, mockGraph,
                TestDependency.class.getDeclaredMethod("setDependency", String.class).getParameters());
    }

    @Test
    public void testInvalidInject() {
        Object[] instances = {
                new StaticMethod(),
                new AbstractMethodImpl(),
                new NonPublicMethod()
        };

        for (Object instance : instances)
            assertThrows(InjectionException.class, () -> methodInjector.injectMethods("test", instance));
    }

    private static class TestDependency {
        private String dependency;
        private boolean nonMethodCalled;

        @Inject
        public void setDependency(String dependency) {
            this.dependency = dependency;
        }

        public void nonInjectMethod(int x, int y) {
            // this will be ignored
            nonMethodCalled = true;
        }
    }

    private static class StaticMethod {
        @Inject
        public static void invalid(String s1) {}
    }

    private static abstract class AbstractMethod {
        @Inject
        public abstract void invalid(String s1);
    }

    private static class AbstractMethodImpl extends AbstractMethod {
        public void invalid(String s1) {}
    }

    private static class NonPublicMethod {
        @Inject
        private void invalid(String s1) {}
    }
}