package io.github.edwardUL99.inject.lite.container;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.exceptions.ContainerException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.container.RunnableContainerBuilder;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * A container is a single unit of execution with its own global injector and annotation scanners.
 * This interface represents the public API to use, should not be extended by client code. Use the builder
 * provided by {@link #builder()} to create an instance
 *
 */
public interface Container {
    /**
     * Get the unit of execution this container is executing
     * @return the container's execution unit
     */
    ExecutionUnit getExecutionUnit();

    /**
     * Get the injector in use by the container. Current implementation
     * returns the same value as {@link Injection#globalInjector()}.
     * This may be null until the container is started. When the container starts, it should set
     * the injector instance
     * @return the injector in use by the container
     */
    Injector getInjector();

    /**
     * Get a list of annotation processors to use that are executed before the execution unit is started
     * @return list of processors
     */
    List<ContainerAnnotationProcessor<? extends Annotation>> getAnnotationProcessors();

    /**
     * Determines if the container's environment should be kept alive until a subsequent call
     * with false if the execution unit completes.
     * @param keepAlive true to keep alive, false to not (or if unit has finished, clear out its environment)
     */
    void setKeepAlive(boolean keepAlive);

    /**
     * Await the container completion
     * @throws ContainerException if an exception in the container occurs
     */
    void await() throws ContainerException;

    /**
     * Get the container's ID
     * @return container ID
     */
    String getId();

    /**
     * Get an executor that creates all child threads in the context of this container
     * @return the container-safe executor
     */
    AsynchronousExecutor asyncExecutor();

    /**
     * Get a builder to create a container
     * @return the builder instance
     */
    static ContainerBuilder builder() {
        return new RunnableContainerBuilder();
    }
}
