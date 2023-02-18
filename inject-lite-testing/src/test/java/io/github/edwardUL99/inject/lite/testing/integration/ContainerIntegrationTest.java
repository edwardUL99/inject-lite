package io.github.edwardUL99.inject.lite.testing.integration;

import io.github.edwardUL99.inject.lite.config.ConfigurationBuilder;
import io.github.edwardUL99.inject.lite.container.Container;
import io.github.edwardUL99.inject.lite.container.ContainerBuilder;
import io.github.edwardUL99.inject.lite.container.ContainerContext;
import io.github.edwardUL99.inject.lite.container.Containers;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.testing.integration.containers.Common;
import io.github.edwardUL99.inject.lite.testing.integration.containers.ContainerOneDependency;
import io.github.edwardUL99.inject.lite.testing.integration.containers.ContainerTwoDependency;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContainerIntegrationTest {
    private final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
            .withInjectionPackagePrefixes("io.github.edwardUL99.inject.lite.testing.integration.containers")
            .withSingleLevelInjection(false);
    private final ContainerBuilder containerOneBuilder = Container.builder()
            .withId("containerOne")
            .withExecutionUnit(container -> assertContainerOne(container, container.getInjector(), true));
    private final ContainerBuilder containerTwoBuilder = Container.builder()
            .withId("containerTwo")
            .withExecutionUnit(container -> assertContainerTwo(container, container.getInjector(), true));

    private void assertContainerOne(Container container, Injector injector, boolean reassert) {
        ContainerOneDependency containerOneDependency = injector.inject("containerOneDependency", ContainerOneDependency.class);
        assertNotNull(containerOneDependency);
        Common common = injector.inject("common", Common.class);
        assertNotNull(common);
        assertThrows(DependencyNotFoundException.class,
                () -> injector.inject("containerTwoDependency", ContainerTwoDependency.class));

        if (reassert) {
            // verify same dependencies are retrieved in child async task
            AsynchronousExecutor executor = container.asyncExecutor();
            executor.scheduleAwait(() -> assertContainerOne(container, Injector.get(), false));
            executor.shutdown();
        }
    }

    private void assertContainerTwo(Container container, Injector injector, boolean reassert) {
        ContainerTwoDependency containerTwoDependency = injector.inject("containerTwoDependency", ContainerTwoDependency.class);
        assertNotNull(containerTwoDependency);
        assertThrows(DependencyNotFoundException.class,
                () -> injector.inject("common", Common.class));
        assertThrows(DependencyNotFoundException.class,
                () -> injector.inject("containerOneDependency", ContainerOneDependency.class));

        if (reassert) {
            // verify same dependencies are retrieved in child async task
            AsynchronousExecutor executor = container.asyncExecutor();
            executor.scheduleAwait(() -> assertContainerTwo(container, Injector.get(), false));
            executor.shutdown();
        }
    }

    @Test
    public void testContainerFunctionality() {
        try (IntegrationTestContext context = IntegrationTestContext.startIntegrationContext(configurationBuilder)) {
            Injector injector = context.getInjector();
            Common common = injector.inject(Common.class);
            assertNotNull(common);
            assertThrows(DependencyNotFoundException.class,
                    () -> injector.inject("containerOneDependency", ContainerOneDependency.class));
            assertThrows(DependencyNotFoundException.class,
                    () -> injector.inject("containerTwoDependency", ContainerTwoDependency.class));

            try (ContainerContext ignored = new ContainerContext()) {
                Containers.executeContainer(containerOneBuilder)
                        .await();
                Containers.executeContainer(containerTwoBuilder)
                        .await();
            }
        }
    }
}
