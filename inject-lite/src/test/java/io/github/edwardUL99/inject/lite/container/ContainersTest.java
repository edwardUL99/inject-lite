package io.github.edwardUL99.inject.lite.container;

import io.github.edwardUL99.inject.lite.internal.container.ContainersInternal;
import io.github.edwardUL99.inject.lite.internal.container.RunnableContainer;
import io.github.edwardUL99.inject.lite.internal.container.RunnableContainerBuilder;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.getInternalState;

public class ContainersTest {
    private static AsynchronousExecutor oldExecutor;
    private AsynchronousExecutor mockExecutor;

    @BeforeAll
    public static void staticInit() {
        ContainerContext.inContext = true;
        oldExecutor = getInternalState(Containers.class, "executor");
    }

    @AfterAll
    public static void staticTeardown() {
        ContainerContext.inContext = false;
        Containers.setExecutor(oldExecutor);
    }

    @BeforeEach
    public void init() {
        mockExecutor = mock(AsynchronousExecutor.class);
        Containers.setExecutor(mockExecutor);
    }

    @Test
    public void testExecuteContainer() {
        RunnableContainer mockContainer = mock(RunnableContainer.class);
        RunnableContainerBuilder mockBuilder = mock(RunnableContainerBuilder.class);

        when(mockBuilder.withExecutor(mockExecutor))
                .thenReturn(mockBuilder);
        when(mockBuilder.build())
                .thenReturn(mockContainer);

        try (MockedStatic<ContainersInternal> containersInternal = mockStatic(ContainersInternal.class)) {
            Container container = Containers.executeContainer(mockBuilder);

            assertSame(mockContainer, container);
            verify(mockContainer).start();
            containersInternal.verify(() -> ContainersInternal.registerContainer(mockContainer));
        }
    }

    @Test
    public void testExecuteSingleContainer() {
        RunnableContainerBuilder mockBuilder = mock(RunnableContainerBuilder.class);

        try (MockedStatic<Containers> containers = mockStatic(Containers.class)) {
            containers.when(() -> Containers.executeSingleContainer(mockBuilder))
                    .thenCallRealMethod();

            Containers.executeSingleContainer(mockBuilder);

            containers.verify(() -> Containers.executeContainer(mockBuilder));
            containers.verify(Containers::awaitContainerFinish);
        }
    }

    @Test
    public void testAwaitContainerFinish() {
        RunnableContainer mockContainer = mock(RunnableContainer.class);
        List<Container> containers = Collections.singletonList(mockContainer);

        when(mockContainer.shouldAwait())
                .thenReturn(true);

        try (MockedStatic<ContainersInternal> containersInternal = mockStatic(ContainersInternal.class)) {
            containersInternal.when(ContainersInternal::getRegisteredContainers)
                    .thenReturn(containers);

            Containers.awaitContainerFinish();

            verify(mockContainer).await();
            verify(mockExecutor).shutdown();
            containersInternal.verify(ContainersInternal::clearContainers);
        }
    }

    @Test
    public void testGetCurrentContainer() {
        RunnableContainer mockContainer = mock(RunnableContainer.class);
        List<Container> containers = Collections.singletonList(mockContainer);

        try (MockedStatic<ContainersInternal> containersInternal = mockStatic(ContainersInternal.class)) {
            containersInternal.when(ContainersInternal::getRegisteredContainers)
                    .thenReturn(containers);

            when(mockContainer.isCurrentContainer())
                    .thenReturn(true);

            Container current = Containers.getCurrentContainer();
            assertSame(current, mockContainer);
            verify(mockContainer).isCurrentContainer();

            reset(mockContainer);
            when(mockContainer.isCurrentContainer())
                    .thenReturn(false);

            current = Containers.getCurrentContainer();
            assertNull(current);
            verify(mockContainer).isCurrentContainer();

            containersInternal.when(ContainersInternal::getRegisteredContainers)
                    .thenReturn(Collections.emptyList());

            assertNull(Containers.getCurrentContainer());

            containersInternal.verify(ContainersInternal::getRegisteredContainers, times(3));
        }
    }
}
