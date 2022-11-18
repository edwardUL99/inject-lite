package io.github.edwardUL99.inject.lite.container;

import io.github.edwardUL99.inject.lite.exceptions.ContainerException;
import io.github.edwardUL99.inject.lite.internal.container.ContainerThreadFactory;
import io.github.edwardUL99.inject.lite.internal.container.ContainersInternal;
import io.github.edwardUL99.inject.lite.internal.container.RunnableContainer;
import io.github.edwardUL99.inject.lite.internal.container.RunnableContainerBuilder;
import io.github.edwardUL99.inject.lite.internal.threads.ExecutorServiceExecutor;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;

// TODO see if you can write tests for containers and other multithreaded functionality

/**
 * A class for running injection containers where each container is a single unit of execution with its own global injectors and
 * annotation scanners. Note that if you wish to use asynchronous code inside containers,
 * use the {@link Container#asyncExecutor()} to get an executor that you can submit tasks to as this executor
 * is container aware, i.e. child threads will use the same parent thread of the container
 */
public final class Containers {
    /**
     * Container executor
     */
    private static AsynchronousExecutor executor = new ExecutorServiceExecutor(new ContainerThreadFactory(null));
    
    private Containers() {}

    /**
     * Set the executor to use for executing container execution units. Not recommended for external use. Simply provided
     * for testing purposes
     * @param executor the executor to set
     */
    static void setExecutor(AsynchronousExecutor executor) {
        Containers.executor = executor;
    }

    /**
     * Execute the unit in the container. Allows execution of multiple containers. You should call
     * {@link #awaitContainerFinish()} at some point to wait for all to finish. The provided scanners will be executed before
     * calling executionUnit
     * @param containerBuilder the builder template to create the container from
     * @return the returned container
     */
    public static Container executeContainer(ContainerBuilder containerBuilder) {
        if (!ContainerContext.isInContext()) {
            throw new ContainerException("You are not executing the container inside a ContainerContext");
        } else {
            Container container = ((RunnableContainerBuilder) containerBuilder).withExecutor(executor).build();

            if (!(container instanceof RunnableContainer))
                throw new ContainerException("The provided ContainerBuilder built an unsupported Container implementation");

            RunnableContainer runnable = (RunnableContainer) container;
            ContainersInternal.registerContainer(runnable);
            runnable.start();

            return container;
        }
    }

    /**
     * Retrieve a container context instance. You should only create one per application as the close method calls
     * a global container await finish
     * @return the container context instance
     */
    public static ContainerContext context() {
        return new ContainerContext();
    }

    /**
     * This method executes a single container and awaits the completion of that container. Useful if you only need
     * one container context and do not want to manually call {@link #awaitContainerFinish()}. Doesn't return anything
     * since it immediately awaits, however the container instance is passed to the execution unit execute method. The provided processors will be executed before
     * calling executionUnit
     * @param containerBuilder the builder template to create the container
     */
    public static void executeSingleContainer(ContainerBuilder containerBuilder) {
        executeContainer(containerBuilder);
        awaitContainerFinish();
    }

    /**
     * Await for all containers to finish
     * @throws ContainerException if an exception in a container occurs
     */
    public static void awaitContainerFinish() throws ContainerException {
        try {
            for (RunnableContainer container : ContainersInternal.getRegisteredContainers()) {
                if (container.shouldAwait())
                    container.await();
            }

        } finally {
            executor.shutdown();
            ContainersInternal.clearContainers();
        }
    }

    /**
     * Get the current container based on the context of where this method is called from
     * @return current container, or null if not found
     */
    public static Container getCurrentContainer() {
        return ContainersInternal.getRegisteredContainers().stream()
                .filter(RunnableContainer::isCurrentContainer)
                .findFirst()
                .orElse(null);
    }
}
