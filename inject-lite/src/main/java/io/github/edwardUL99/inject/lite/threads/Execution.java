package io.github.edwardUL99.inject.lite.threads;

import io.github.edwardUL99.inject.lite.exceptions.AsyncException;

/**
 * An execution represents an executor that is currently running which has been started
 * by the AsynchronousExecutor class
 */
public interface Execution {
    /**
     * Await the result of the execution. This asynchronously waits for the execution to
     * finish
     * @throws AsyncException if the result fails to be retrieved
     */
    void awaitFinish() throws AsyncException;
}
