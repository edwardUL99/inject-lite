package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

/**
 * Used to provide entry points for the default implementations of {@link HookSupport}
 */
public final class InjectorHooks {
    /**
     * The pre construct hook handler
     */
    private static HookHandler preConstruct;
    /**
     * The post construct hook handler
     */
    private static HookHandler postConstruct;

    static {
        setup();
    }

    static void setPreConstruct(HookHandler preConstruct) {
        InjectorHooks.preConstruct = preConstruct;
    }

    static void setPostConstruct(HookHandler postConstruct) {
        InjectorHooks.postConstruct = postConstruct;
    }

    static void setup() {
        preConstruct = new PreConstructHandler();
        postConstruct = new ConstructedHandler();
    }

    /**
     * Handles pre construct hook invocation
     * @param hookSupport the injector supporting hooks
     * @param cls the class being pre-constructed
     */
    public static void handlePreConstruct(HookSupport hookSupport, Class<?> cls) {
        preConstruct.handle(hookSupport, null, cls);
    }

    /**
     * Handles post construct hook invocation
     * @param hookSupport the injector supporting hooks
     * @param instance the instance being injected
     * @param cls the class being post constructed
     */
    public static void handlePostConstruct(HookSupport hookSupport, Object instance, Class<?> cls) {
        postConstruct.handle(hookSupport, instance, cls);
    }

    /**
     * An interface that represents an injector that supports hooks. The hooks are ones that are directly supported
     * by the injector. Other hooks like lazy invocation is handled by the InjectionInvocationProxy, so not a part of the
     */
    public interface HookSupport extends InternalInjector {
        /**
         * Do the pre construct hook
         * @param cls the type of the dependency
         */
        default void doPreConstruct(Class<?> cls) {
            InjectorHooks.handlePreConstruct(this, cls);
        }

        /**
         * Do the post construct hook
         * @param instance the instance being injected
         * @param cls the type of the dependency
         */
        default void doPostConstruct(Object instance, Class<?> cls) {
            InjectorHooks.handlePostConstruct(this, instance, cls);
        }
    }
}
