package io.github.edwardUL99.inject.lite.internal.container;

import io.github.edwardUL99.inject.lite.annotations.ContainerInject;
import io.github.edwardUL99.inject.lite.container.Container;
import io.github.edwardUL99.inject.lite.container.Containers;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.ScannersContext;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategy;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.threads.Threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Provides the API for any internal containers implementation not to be exposed to public API
 */
public final class ContainersInternal {
    /**
     * The list of threads running containers
     */
    private static final List<RunnableContainer> containers = new CopyOnWriteArrayList<>();
    /**
     * Determines if container inject annotations are enabled
     */
    private static boolean containerInjectEnabled = true;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (RunnableContainer container : containers)
                container.stop();
        }));
    }

    private ContainersInternal() {}

    /**
     * Register the container with the context
     * @param container the container to register
     */
    public static void registerContainer(RunnableContainer container) {
        containers.add(container);
    }

    /**
     * Removes the registered container
     * @param container the registered container
     */
    public static void removeContainer(RunnableContainer container) {
        synchronized (containers) {
            containers.remove(container);
        }
    }

    /**
     * Get the registered containers
     * @return list of registered containers
     */
    public static List<RunnableContainer> getRegisteredContainers() {
        return Collections.unmodifiableList(containers);
    }

    /**
     * Set the value for container inject enabled or disabled
     * @param containerInjectEnabled true if enabled, false if not
     */
    public static void setContainerInjectEnabled(boolean containerInjectEnabled) {
        ContainersInternal.containerInjectEnabled = containerInjectEnabled;
    }

    /**
     * Get true if container inject is enabled/disabled
     * @return true if container inject is enabled, false if not
     */
    public static boolean isContainerInjectEnabled() {
        return containerInjectEnabled;
    }

    /**
     * Tears down before a container finishes
     */
    public static void teardownAfterContainer(Thread thread) {
        thread = Threads.getInjectionThread(thread);
        InjectionContext.destroySingletonInjector(thread);
        ScannersContext.destroySingletonScanner(thread);
    }

    /**
     * Clears all registered containers
     */
    public static void clearContainers() {
        containers.clear();
    }

    private static boolean shouldRegisterContainerDependency(String[] ids) {
        List<String> idsList = new ArrayList<>(Arrays.asList(ids));
        Container container = Containers.getCurrentContainer();

        String id;
        return container != null && (idsList.size() == 0 ||
                ((id = container.getId()) != null && idsList.contains(id)));
    }

    /**
     * Register a dependency to the provided injector while checking if it should be injected only in certain containers
     * if the class cls is annotated with ContainerInject
     * @param injector the injector to register the dependency to
     * @param strategy the strategy to register the dependency to the injector
     * @param cls the dependency class
     * @return true if injected
     */
    public static boolean registerDependencyCheckingContainer(Injector injector, RegistrationStrategy strategy, Class<?> cls) {
        boolean injectNormal = true;
        ContainerInject containerInject = cls.getAnnotation(ContainerInject.class);

        if (isContainerInjectEnabled() && containerInject != null) {
            boolean containerThread = Threads.isContainerThread(Thread.currentThread());
            injectNormal = !containerInject.containerOnly() && !containerThread;

            if (containerThread) {
                if (shouldRegisterContainerDependency(containerInject.value())) {
                    strategy.register(injector);
                    return true;
                }
            }
        }

        if (injectNormal) {
            strategy.register(injector);
            return true;
        }

        return false;
    }
}
