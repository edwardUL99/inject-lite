package io.github.edwardUL99.inject.lite.internal.dependency;

import io.github.edwardUL99.inject.lite.internal.hooks.InjectorHooks;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

/**
 * A base class for injectable dependencies
 */
public abstract class BaseInjectableDependency implements InjectableDependency {
    /**
     * The name of the dependency
     */
    protected final String name;
    /**
     * The type of the object
     */
    protected final Class<?> type;
    /**
     * The injector implementation
     */
    protected final InternalInjector injector;
    /**
     * HookSupport instance of injector. If null, the injector does not support hook methods
     */
    protected final InjectorHooks.HookSupport hookSupport;
    /**
     * Indicates that this proxy should be created as a singleton
     */
    protected final boolean singleton;

    /**
     * Instantiate the dependency
     * @param name the name of the dependency
     * @param type the type of the injectable object
     * @param injector the injector for injecting dependencies
     */
    public BaseInjectableDependency(String name, Class<?> type, InternalInjector injector) {
        this(name, type, injector, true);
    }

    /**
     * Instantiate the dependency
     * @param name the name of the dependency
     * @param type the type of the injectable object
     * @param injector the injector for injecting dependencies
     * @param singleton determines if this dependency is a singleton or not (only used when singleton behaviour is enabled)
     */
    public BaseInjectableDependency(String name, Class<?> type, InternalInjector injector, boolean singleton) {
        this.name = name;
        this.type = type;
        this.injector = injector;
        this.singleton = singleton;
        this.hookSupport = (this.injector instanceof InjectorHooks.HookSupport) ?
                (InjectorHooks.HookSupport) injector : null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}
