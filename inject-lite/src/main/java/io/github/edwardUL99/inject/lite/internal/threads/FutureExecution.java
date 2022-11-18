package io.github.edwardUL99.inject.lite.internal.threads;

import io.github.edwardUL99.inject.lite.exceptions.AsyncException;
import io.github.edwardUL99.inject.lite.threads.Execution;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class represents an execution that wraps a Future object
 */
public class FutureExecution implements Execution {
    /**
     * The wrapped future
     */
    private final Future<?> future;

    /**
     * Create the execution with the wrapped future
     * @param future the future to wrap
     */
    public FutureExecution(Future<?> future) {
        this.future = future;
    }

    @Override
    public void awaitFinish() throws AsyncException {
        try {
            Object received = future.get();

            if (received != null) {
                throw new AsyncException("Future execution returned without fully completing");
            }
        } catch (CancellationException | ExecutionException | InterruptedException ex) {
            throw new AsyncException("Exception occurred while awaiting finish", ex);
        }
    }

    /**
     * A utility method for wrapping the provided future
     * @param future the wrapped future
     * @return the execution instance
     */
    public static FutureExecution wrap(Future<?> future) {
        return new FutureExecution(future);
    }
}
