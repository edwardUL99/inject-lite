package io.github.edwardUL99.inject.lite.internal.container;

import io.github.edwardUL99.inject.lite.internal.threads.SharedInjectionThread;

import java.util.concurrent.ThreadFactory;

/**
 * Factory used for creating container threads for the ExecutorService API
 */
public class ContainerThreadFactory implements ThreadFactory {
    /**
     * Thread running a parent container if not null
     */
    private final Thread containerThread;

    /**
     * Create an instance
     * @param containerThread thread running container if this is a child thread
     */
    public ContainerThreadFactory(Thread containerThread) {
        this.containerThread = containerThread;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread;

        if (containerThread == null) {
            thread = new ContainerInjectionThread(runnable);
        } else {
            // we want to share container injector with child threads
            thread = new SharedInjectionThread(runnable, containerThread);
        }

        return thread;
    }
}
