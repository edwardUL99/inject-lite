package io.github.edwardUL99.inject.lite.internal.threads;

import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import io.github.edwardUL99.inject.lite.threads.Execution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * An executor using Java's ExecutorService API
 */
public class ExecutorServiceExecutor implements AsynchronousExecutor {
    /**
     * The executor service to execute
     */
    protected final ExecutorService executorService;

    /**
     * Construct an instance using the provided thread as parent
     * @param injectionThread the injection thread this thread is running under
     */
    public ExecutorServiceExecutor(Thread injectionThread) {
        this(runnable -> new SharedInjectionThread(runnable, injectionThread));
    }

    /**
     * Create an executor with the provided factory
     * @param threadFactory the thread factory
     */
    public ExecutorServiceExecutor(ThreadFactory threadFactory) {
        this.executorService = Executors.newCachedThreadPool(threadFactory);
    }

    @Override
    public Execution schedule(Runnable runnable) {
        return FutureExecution.wrap(executorService.submit(runnable));
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }
}
