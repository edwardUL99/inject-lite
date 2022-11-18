package io.github.edwardUL99.inject.lite.container;

/**
 * A context which automatically awaits and finishes all containers. Should be used when you wish to execute containers
 */
public final class ContainerContext implements AutoCloseable {
    /**
     * Determines if the context is enabled
     */
    static boolean inContext;

    public ContainerContext() {
        inContext = true;
    }

    /**
     * Determines if in context
     * @return true if in context
     */
    static boolean isInContext() {
        return inContext;
    }

    @Override
    public void close() {
        inContext = false;
        Containers.awaitContainerFinish();
    }
}
