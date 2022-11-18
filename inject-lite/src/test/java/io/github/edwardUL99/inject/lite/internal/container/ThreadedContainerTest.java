package io.github.edwardUL99.inject.lite.internal.container;

import io.github.edwardUL99.inject.lite.container.Containers;
import io.github.edwardUL99.inject.lite.exceptions.AsyncException;
import io.github.edwardUL99.inject.lite.internal.threads.ExecutorServiceExecutor;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import io.github.edwardUL99.inject.lite.container.ExecutionUnit;
import io.github.edwardUL99.inject.lite.exceptions.ContainerException;
import io.github.edwardUL99.inject.lite.threads.Execution;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ThreadedContainerTest {
    private ThreadedContainer container;
    private AsynchronousExecutor mockExecutor;
    private Execution mockExecution;
    private MockedStatic<ContainersInternal> mockedContainers;

    @BeforeEach
    public void init() {
        mockExecutor = mock(AsynchronousExecutor.class);
        mockExecution = mock(Execution.class);
        mockedContainers = mockStatic(ContainersInternal.class);
        container = new ThreadedContainer(mockExecutor, mock(ExecutionUnit.class), new ArrayList<>(), "id");
    }

    @AfterEach
    public void teardown() {
        mockedContainers.close();
    }

    @Test
    public void testContainerStartStop() {
        when(mockExecutor.schedule(any(Runnable.class)))
                .thenReturn(mockExecution);

        container.start();
        container.stop();

        verify(mockExecutor).schedule(any(Runnable.class));
        mockedContainers.verify(() -> ContainersInternal.teardownAfterContainer(any(Thread.class)));
    }

    @Test
    public void testAsyncExecutor() {
        assertInstanceOf(ExecutorServiceExecutor.class, container.asyncExecutor());
    }

    @Test
    public void testContainerAwait() {
        when(mockExecutor.schedule(any(Runnable.class)))
                .thenReturn(mockExecution);

        assertTrue(container.shouldAwait());
        container.await(); // shouldn't call future since future is not opened yet
        container.start();
        assertTrue(container.shouldAwait());
        container.await();
        assertFalse(container.shouldAwait());
        container.start();
        assertTrue(container.shouldAwait());

        doThrow(AsyncException.class).when(mockExecution).awaitFinish();

        assertThrows(ContainerException.class, () -> container.await());
        assertFalse(container.shouldAwait());

        verify(mockExecution, times(2)).awaitFinish();
    }
}
