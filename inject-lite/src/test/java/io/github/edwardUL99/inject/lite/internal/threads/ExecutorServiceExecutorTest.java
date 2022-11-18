package io.github.edwardUL99.inject.lite.internal.threads;

import io.github.edwardUL99.inject.lite.threads.Execution;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExecutorServiceExecutorTest {
    @Test
    public void testCreateWithThread() {
        try (MockedStatic<Executors> executors = mockStatic(Executors.class)) {
            new ExecutorServiceExecutor(new Thread(mock(Runnable.class)));
            executors.verify(() -> Executors.newCachedThreadPool(any(ThreadFactory.class)));
        }
    }

    @Test
    public void testCreateWithThreadFactory() {
        try (MockedStatic<Executors> executors = mockStatic(Executors.class)) {
            ThreadFactory factory = mock(ThreadFactory.class);
            new ExecutorServiceExecutor(factory);
            executors.verify(() -> Executors.newCachedThreadPool(factory));
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testSchedule() {
        try (MockedStatic<Executors> executors = mockStatic(Executors.class)) {
            ThreadFactory factory = mock(ThreadFactory.class);
            ExecutorService mockService = mock(ExecutorService.class);
            executors.when(() -> Executors.newCachedThreadPool(factory))
                    .thenReturn(mockService);

            ExecutorServiceExecutor executor = new ExecutorServiceExecutor(factory);

            Runnable mockRunnable = mock(Runnable.class);
            Future mockFuture = mock(Future.class);
            when(mockService.submit(mockRunnable))
                    .thenReturn(mockFuture);

            Execution execution = executor.schedule(mockRunnable);

            assertInstanceOf(FutureExecution.class, execution);
            verify(mockService).submit(mockRunnable);
            executors.verify(() -> Executors.newCachedThreadPool(factory));
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testScheduleAwait() {
        try (MockedStatic<Executors> executors = mockStatic(Executors.class);
            MockedStatic<FutureExecution> futureException = mockStatic(FutureExecution.class)) {
            ThreadFactory factory = mock(ThreadFactory.class);
            ExecutorService mockService = mock(ExecutorService.class);
            executors.when(() -> Executors.newCachedThreadPool(factory))
                    .thenReturn(mockService);

            ExecutorServiceExecutor executor = new ExecutorServiceExecutor(factory);

            Runnable mockRunnable = mock(Runnable.class);
            Future mockFuture = mock(Future.class);
            FutureExecution mockExecution = mock(FutureExecution.class);
            when(mockService.submit(mockRunnable))
                    .thenReturn(mockFuture);
            futureException.when(() -> FutureExecution.wrap(mockFuture))
                    .thenReturn(mockExecution);

            executor.scheduleAwait(mockRunnable);

            verify(mockExecution).awaitFinish();
            verify(mockService).submit(mockRunnable);
            executors.verify(() -> Executors.newCachedThreadPool(factory));
        }
    }

    @Test
    public void testShutdown() {
        try (MockedStatic<Executors> executors = mockStatic(Executors.class)) {
            ThreadFactory factory = mock(ThreadFactory.class);
            ExecutorService mockService = mock(ExecutorService.class);
            executors.when(() -> Executors.newCachedThreadPool(factory))
                    .thenReturn(mockService);

            ExecutorServiceExecutor executor = new ExecutorServiceExecutor(factory);
            executor.shutdown();

            verify(mockService).shutdown();
            executors.verify(() -> Executors.newCachedThreadPool(factory));
        }
    }
}
