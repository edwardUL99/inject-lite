package io.github.edwardUL99.inject.lite.container;

/**
 * The unit of execution to execute inside a container
 */
@FunctionalInterface
public interface ExecutionUnit {
    /**
     * Executes the unit
     * @param container the container being executed
     */
    void execute(Container container);
}
