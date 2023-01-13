package io.github.edwardUL99.inject.lite.internal.container;

import io.github.edwardUL99.inject.lite.internal.threads.ParentThread;

/**
 * A shared injection thread for containers
 */
public class ContainerInjectionThread extends ParentThread {
    /**
     * Create the thread
     * @param runnable the runnable task
     */
    public ContainerInjectionThread(Runnable runnable) {
        super(runnable);
    }
}
