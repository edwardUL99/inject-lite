package io.github.edwardUL99.inject.lite.internal.container;

import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationProcessor;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationScanner;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationScanners;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import io.github.edwardUL99.inject.lite.container.ContainerAnnotationProcessor;
import io.github.edwardUL99.inject.lite.container.ContainerBuilder;
import io.github.edwardUL99.inject.lite.container.ExecutionUnit;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation to build runnable containers
 */
public class RunnableContainerBuilder implements ContainerBuilder {
    /**
     * Execution unit to run
     */
    protected ExecutionUnit executionUnit;
    /**
     * List of annotation processors
     */
    protected List<ContainerAnnotationProcessor<? extends Annotation>> processors = new ArrayList<>();
    /**
     * Flag to indicate that we need to do annotation processing
     */
    protected boolean annotationProcessorsUsed;
    /**
     * Determines if manual scanning is being carried out
     */
    protected boolean manualScan;
    /**
     * The executor for asynchronously executing containers
     */
    protected AsynchronousExecutor executor;
    /**
     * Container ID
     */
    protected String id;

    @Override
    public ContainerBuilder withExecutionUnit(ExecutionUnit executionUnit) {
        this.executionUnit = executionUnit;

        return this;
    }

    @Override
    public ContainerBuilder withAnnotationProcessors(List<ContainerAnnotationProcessor<? extends Annotation>> processors) {
        this.processors = processors;
        this.annotationProcessorsUsed = processors.size() > 0;

        return this;
    }

    @Override
    public ContainerBuilder withManualAnnotationScan(boolean manualAnnotationScan) {
        this.manualScan = manualAnnotationScan;

        return this;
    }

    @Override
    public ContainerBuilder withId(String id) {
        this.id = id;

        return this;
    }

    /**
     * Set the executor to use
     * @param executor the asynchronous executor
     * @return the builder
     */
    public ContainerBuilder withExecutor(AsynchronousExecutor executor) {
        this.executor = executor;

        return this;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> void registerProcessor(AnnotationScanner scanner, ContainerAnnotationProcessor<? extends Annotation> processor, Class<T> cls) {
        AnnotationProcessor<T> processor1 = (AnnotationProcessor<T>) processor.getProcessor();
        scanner.registerAnnotationProcessor((Class<T>) processor.getAnnotation(), processor1);
    }

    private ExecutionUnit getExecutionUnit() {
        ExecutionUnit unit;

        if (annotationProcessorsUsed) {
            unit = container -> {
                AnnotationScanner scanner = AnnotationScanners.globalScanner();

                for (ContainerAnnotationProcessor<? extends Annotation> processor : processors) {
                    Class<? extends Annotation> annotation = processor.getAnnotation();
                    registerProcessor(scanner, processor, annotation);

                    if (!manualScan)
                        scanner.scan(annotation);
                }

                executionUnit.execute(container);
            };
        } else {
            unit = executionUnit;
        }

        return unit;
    }

    @Override
    public RunnableContainer build() {
        return new ThreadedContainer(executor, getExecutionUnit(), processors, id);
    }
}
