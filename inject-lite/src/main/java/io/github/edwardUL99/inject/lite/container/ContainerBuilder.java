package io.github.edwardUL99.inject.lite.container;

import io.github.edwardUL99.inject.lite.annotations.ContainerInject;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Represents an interface for building containers. No build method is provided to the public API. A builder is passed
 * to the Containers class as a template for creating a Container instance. Not to be extended by client code
 */
public interface ContainerBuilder {
    /**
     * Build the container with the provided unit of execution, defaulting to no-op
     * @param executionUnit the unit of execution
     * @return builder instance
     */
    ContainerBuilder withExecutionUnit(ExecutionUnit executionUnit);

    /**
     * Build the container with the provided annotation processors, defaulting to empty list
     * @param processors the list of processors
     * @return builder instance
     */
    ContainerBuilder withAnnotationProcessors(List<ContainerAnnotationProcessor<? extends Annotation>> processors);

    /**
     * By default, annotations for registered processors are scanned as they are registered. However,
     * if you want to simply register the processors and call the scan method on the globalScanner() yourself manually
     * later in initialisation, pass true here. Remember, if this is true, annotations won't be scanned unless you call
     * scan yourself (and within the container execution unit)
     * @param manualAnnotationScan true to not call scan automatically, false to scan as processors are added
     * @return builder instance
     */
    ContainerBuilder withManualAnnotationScan(boolean manualAnnotationScan);

    /**
     * Specify an ID for this container. IDs are used by {@link ContainerInject}
     * to filter dependencies for containers
     * @param id the ID for the container
     * @return builder instance
     */
    ContainerBuilder withId(String id);

    /**
     * Builds an instance of a container that can be run by the Containers class. Therefore,
     * the implementation returned by this method is an internal detail, and thus, should not be overridden by client code
     * @return the built container
     */
    Container build();
}
