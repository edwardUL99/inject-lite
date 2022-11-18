package io.github.edwardUL99.inject.lite.internal.container;

import io.github.edwardUL99.inject.lite.container.Container;

/**
 * This is the internal API for containers that allows them to be run
 */
public interface RunnableContainer extends Container {
    /**
     * Create the container environment and start the container
     */
    void start();

    /**
     * Teardown the container environment and stop the container
     */
    void stop();

    /**
     * Determines if the container should be awaited on. Always true unless a previous call to await threw an exception
     * @return true to await
     */
    boolean shouldAwait();

    /**
     * Get the container's thread
     * @return the container thread
     */
    Thread getContainerThread();

    /**
     * Determine if this container matches the current thread or parent thread matches
     * @return true if current container, false if not
     */
    boolean isCurrentContainer();
}
