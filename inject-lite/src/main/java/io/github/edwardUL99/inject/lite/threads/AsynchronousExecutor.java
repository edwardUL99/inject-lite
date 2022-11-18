package io.github.edwardUL99.inject.lite.threads;

/**
 * An API for asynchronously executing tasks
 */
public interface AsynchronousExecutor {
    /**
     * Schedule a runnable
     * @param runnable the runnable to schedule
     * @return the object representing the task that will be resolved
     */
    Execution schedule(Runnable runnable);

    /**
     * Schedule a runnable and await the result before returning. Equivalent to:
     * <pre>{@code
     *   Execution execution = executor.schedule(runnable);
     *   execution.await();
     * }</pre>
     * @param runnable the runnable to execute asynchronously
     */
    default void scheduleAwait(Runnable runnable) {
        Execution execution = schedule(runnable);
        execution.awaitFinish();
    }

    /**
     * Shutdown the executor, preferably allowing currently executing tasks to finish
     */
    void shutdown();
}
