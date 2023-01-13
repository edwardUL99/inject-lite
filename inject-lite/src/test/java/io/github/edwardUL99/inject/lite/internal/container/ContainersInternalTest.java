package io.github.edwardUL99.inject.lite.internal.container;

import io.github.edwardUL99.inject.lite.annotations.ContainerInject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.container.Containers;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.ScannersContext;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategy;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.threads.ParentThread;
import io.github.edwardUL99.inject.lite.internal.threads.Threads;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ContainersInternalTest {
    private RunnableContainer mockContainer;
    private RunnableContainer mockContainer1;
    private InternalInjector mockInjector;
    private RegistrationStrategy mockStrategy;

    @BeforeEach
    public void init() {
        mockContainer = mock(RunnableContainer.class);
        mockContainer1 = mock(RunnableContainer.class);
        mockInjector = mock(InternalInjector.class);
        mockStrategy = mock(RegistrationStrategy.class);

        when(mockContainer.getInjector())
                .thenReturn(mockInjector);
        when(mockContainer.getId())
                .thenReturn("testContainer");
        when(mockContainer1.getId())
                .thenReturn("testContainer1");

        ContainersInternal.clearContainers();
    }

    @Test
    public void testRegisterContainer() {
        List<RunnableContainer> containers = ContainersInternal.getRegisteredContainers();
        assertTrue(containers.isEmpty());

        ContainersInternal.registerContainer(mockContainer);

        containers = ContainersInternal.getRegisteredContainers();
        assertEquals(1, containers.size());
    }

    @Test
    public void testClearContainers() {
        List<RunnableContainer> containers = ContainersInternal.getRegisteredContainers();
        assertTrue(containers.isEmpty());

        ContainersInternal.registerContainer(mockContainer);

        containers = ContainersInternal.getRegisteredContainers();
        assertEquals(1, containers.size());

        ContainersInternal.clearContainers();

        assertTrue(ContainersInternal.getRegisteredContainers().isEmpty());
    }

    @Test
    public void testTeardownAfterContainer() {
        try (MockedStatic<Threads> threads = mockStatic(Threads.class);
             MockedStatic<InjectionContext> injectionContext = mockStatic(InjectionContext.class);
             MockedStatic<ScannersContext> scannersContext = mockStatic(ScannersContext.class)) {
            Thread thread = new Thread(mock(Runnable.class));
            injectionContext.when(() -> Threads.getInjectionThread(thread))
                    .thenReturn(thread);

            ContainersInternal.teardownAfterContainer(thread);

            threads.verify(() -> Threads.getInjectionThread(thread));
            injectionContext.verify(() -> InjectionContext.destroySingletonInjector(thread));
            scannersContext.verify(() -> ScannersContext.destroySingletonScanner(thread));
        }
    }

    @Test
    public void testRegisterContainerDependencyAllContainers() {
        try (MockedStatic<Containers> containers = mockStatic(Containers.class);
             MockedStatic<Threads> threads = mockStatic(Threads.class)) {
            ContainerInjectionThread containerInjectionThread =
                    new ContainerInjectionThread(mock(Runnable.class));

            threads.when(Threads::getCurrentThread)
                        .thenReturn(containerInjectionThread);
            threads.when(() -> Threads.isInjectionAwareThread(containerInjectionThread))
                    .thenReturn(true);
            containers.when(Containers::getCurrentContainer)
                            .thenReturn(mockContainer);
            ContainersInternal.registerDependencyCheckingContainer(mockInjector, mockStrategy, TestClass.class);

            ParentThread normalThread = new ParentThread(mock(Runnable.class));

            threads.when(Threads::getCurrentThread)
                    .thenReturn(normalThread);

            containers.when(Containers::getCurrentContainer)
                    .thenReturn(mockContainer1);
            threads.when(() -> Threads.isInjectionAwareThread(any(Thread.class)))
                    .thenReturn(false);
            ContainersInternal.registerDependencyCheckingContainer(mockInjector, mockStrategy, TestClass.class);

            threads.when(() -> Threads.isInjectionAwareThread(any(Thread.class)))
                    .thenReturn(true);
            ContainersInternal.registerDependencyCheckingContainer(mockInjector, mockStrategy, TestClass.class);

            containers.verify(Containers::getCurrentContainer, times(1));
            verify(mockStrategy, times(3)).register(mockInjector);
            verify(mockContainer).getId();
            verifyNoInteractions(mockContainer1);
        }
    }

    @Test
    public void testRegisterContainerDependencySpecifiedContainers() {
        try (MockedStatic<Containers> containers = mockStatic(Containers.class);
             MockedStatic<Threads> threads = mockStatic(Threads.class)) {
            ContainerInjectionThread containerInjectionThread =
                    new ContainerInjectionThread(mock(Runnable.class));

            threads.when(Threads::getCurrentThread)
                    .thenReturn(containerInjectionThread);
            threads.when(() -> Threads.isInjectionAwareThread(any(Thread.class)))
                    .thenReturn(true);
            containers.when(Containers::getCurrentContainer)
                    .thenReturn(mockContainer);
            ContainersInternal.registerDependencyCheckingContainer(mockInjector, mockStrategy, TestClass1.class);

            containers.when(Containers::getCurrentContainer)
                    .thenReturn(mockContainer1);
            ContainersInternal.registerDependencyCheckingContainer(mockInjector, mockStrategy, TestClass1.class);

            containers.verify(Containers::getCurrentContainer, times(2));
            verify(mockStrategy, times(1)).register(mockInjector);
            verify(mockContainer).getId();
            verify(mockContainer).getId();
        }
    }

    @ContainerInject(value = {"testContainer"}, containerOnly = false)
    @Injectable("test")
    private static class TestClass {

    }

    @ContainerInject({"testContainer"})
    @Injectable("test")
    private static class TestClass1 {

    }
}
