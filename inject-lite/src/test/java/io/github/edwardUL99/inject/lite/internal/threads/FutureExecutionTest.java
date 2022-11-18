package io.github.edwardUL99.inject.lite.internal.threads;

import io.github.edwardUL99.inject.lite.exceptions.AsyncException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("rawtypes")
public class FutureExecutionTest {
    private Future mockFuture;
    private FutureExecution execution;

    @BeforeEach
    public void init() {
        mockFuture = mock(Future.class);
        execution = new FutureExecution(mockFuture);
    }

    @Test
    public void testAwaitFinish() throws Exception {
        execution.awaitFinish();

        verify(mockFuture).get();
    }

    @Test
    public void testAwaitFinishWithException() throws Exception {
        doThrow(CancellationException.class).when(mockFuture).get();
        assertThrows(AsyncException.class, () -> execution.awaitFinish());

        doThrow(ExecutionException.class).when(mockFuture).get();
        assertThrows(AsyncException.class, () -> execution.awaitFinish());

        doThrow(InterruptedException.class).when(mockFuture).get();
        assertThrows(AsyncException.class, () -> execution.awaitFinish());
        verify(mockFuture, times(3)).get();

        reset(mockFuture);

        when(mockFuture.get())
                .thenReturn("hello");
        assertThrows(AsyncException.class, () -> execution.awaitFinish());

        verify(mockFuture).get();
    }
}
