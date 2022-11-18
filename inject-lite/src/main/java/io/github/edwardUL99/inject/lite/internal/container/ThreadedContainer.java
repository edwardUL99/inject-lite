package io.github.edwardUL99.inject.lite.internal.container;

import io.github.edwardUL99.inject.lite.exceptions.AsyncException;
import io.github.edwardUL99.inject.lite.internal.threads.ExecutorServiceExecutor;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import io.github.edwardUL99.inject.lite.container.ContainerAnnotationProcessor;
import io.github.edwardUL99.inject.lite.container.ExecutionUnit;
import io.github.edwardUL99.inject.lite.exceptions.ContainerException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.threads.Threads;
import io.github.edwardUL99.inject.lite.threads.Execution;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * The multithreaded implementation of the container
 */
public class ThreadedContainer implements RunnableContainer {
    /**
     * The unit of execution to execute in the container
     */
    private final ExecutionUnit executionUnit;
    /**
     * Determines if the execution unit completed
     */
    private boolean unitCompleted;
    /**
     * Determines if the container should be kept alive after the unit completes
     */
    private boolean keepAlive;
    /**
     * The container ID
     */
    private final String id;
    /**
     * List of annotation processors
     */
    private final List<ContainerAnnotationProcessor<? extends Annotation>> processors;
    /**
     * Used to asynchronously execute the container
     */
    private final AsynchronousExecutor executor;
    /**
     * The injector used
     */
    private Injector injector;
    /**
     * Container thread
     */
    private Thread containerThread;
    /**
     * The execution from the executor
     */
    private Execution execution;
    /**
     * Determines if the container should be awaited on
     */
    private boolean shouldAwait = true;

    /**
     * Create an instance of container
     * @param executor the executor to multithreaded the container with
     * @param executionUnit the container's unit of execution
     * @param processors the list of annotation processors
     * @param id container ID
     */
    ThreadedContainer(AsynchronousExecutor executor, ExecutionUnit executionUnit,
                             List<ContainerAnnotationProcessor<? extends Annotation>> processors, String id) {
        this.executionUnit = unitWrapper(executionUnit);
        this.executor = executor;
        this.processors = processors;
        this.id = id;
    }

    private ExecutionUnit unitWrapper(ExecutionUnit executionUnit) {
        return container -> {
            this.containerThread = Thread.currentThread();
            this.injector = Injector.get();
            executionUnit.execute(container);
            this.unitCompleted = true;
            this.containerThread = null;

            if (!keepAlive)
                stop();
        };
    }

    @Override
    public void setKeepAlive(boolean keepAlive) {
        if (this.unitCompleted && this.keepAlive && !keepAlive) {
            stop();
        } else {
            this.keepAlive = keepAlive;
        }
    }

    @Override
    public AsynchronousExecutor asyncExecutor() {
        return new ExecutorServiceExecutor(new ContainerThreadFactory(getContainerThread()));
    }

    @Override
    public void start() {
        this.shouldAwait = true;
        this.execution = this.executor.schedule(() -> executionUnit.execute(this));
    }

    @Override
    public void stop() {
        ContainersInternal.removeContainer(this);
        ContainersInternal.teardownAfterContainer(Threads.getCurrentThread());
    }

    @Override
    public boolean shouldAwait() {
        return shouldAwait;
    }

    @Override
    public void await() throws ContainerException {
        if (execution != null) {
            try {
                execution.awaitFinish();
                execution = null;
                shouldAwait = false;
            } catch (AsyncException ex) {
                shouldAwait = false;
                Throwable cause = ex.getCause();

                if (cause instanceof ExecutionException)
                    throw new ContainerException("An exception occurred inside in the injection container", cause.getCause());
                else
                    throw new ContainerException("An unknown exception occurred awaiting for the container to complete", ex);
            }
        }
    }

    @Override
    public ExecutionUnit getExecutionUnit() {
        return executionUnit;
    }

    @Override
    public Injector getInjector() {
        return injector;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Thread getContainerThread() {
        return containerThread;
    }

    @Override
    public boolean isCurrentContainer() {
        return Objects.equals(Thread.currentThread(), containerThread)
                || Objects.equals(Threads.getCurrentThread(), containerThread);
    }

    @Override
    public List<ContainerAnnotationProcessor<? extends Annotation>> getAnnotationProcessors() {
        return processors;
    }
}
