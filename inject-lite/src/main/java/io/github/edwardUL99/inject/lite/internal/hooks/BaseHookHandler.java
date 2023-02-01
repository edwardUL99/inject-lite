package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.exceptions.HookException;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An abstract hook handler to commonalise code
 */
public abstract class BaseHookHandler implements HookHandler {
    @Override
    public final void handle(InternalInjector injector, Object instance, Class<?> cls) {
        Class<? extends Annotation> hookAnnotation = getAnnotationHook();

        if (hookAnnotation != null)
            executeAnnotatedHooks(hookAnnotation, injector, instance, cls);

        if (ReflectionUtils.isAssignable(getHookType(), cls)) {
            handleInterfacedMethods(injector, instance, cls);
        }
    }

    private void executeAnnotatedHooks(Class<? extends Annotation> hookAnnotation, InternalInjector injector,
                                       Object instance, Class<?> cls) {
        handleAnnotatedMethods(injector, instance,
            Arrays.stream(cls.getMethods())
                    .filter(m -> m.getAnnotation(hookAnnotation) != null)
                    .peek(m -> {
                        String error = validateAnnotationHookParameters(m);

                        if (error != null)
                            throw new HookException("Argument validation of hook method: " + m.getName() +
                                    " failed: " + error);
                    })
                    .collect(Collectors.toList()));
    }

    /**
     * Get the hook this handler is meant to handle
     * @return the hook type
     */
    protected abstract Class<? extends Hook> getHookType();

    /**
     * If this method returns an annotation class, not null, the handler will find all methods annotated with it and
     * pass them into {@link #handleAnnotatedMethods(InternalInjector, Object, List)}
     * @return the annotation hook class
     */
    protected Class<? extends Annotation> getAnnotationHook() {
        return null;
    }

    /**
     * If this method returns a String error message, it will be thrown as an exception
     * @param method the method to validate parameters of
     * @return the error message or null if no error
     */
    protected String validateAnnotationHookParameters(Method method) {
        return null;
    }

    /**
     * Hook to handle methods annotated as hooks. Default implementation is no-op
     * @param injector the injector instance
     * @param instance the object instance, can be null
     * @param methods the list of matching methods
     */
    protected void handleAnnotatedMethods(InternalInjector injector, Object instance, List<Method> methods) {}

    /**
     * Called if the class is of the supported hook interface and calls the interfaced methods
     * @param injector the injector
     * @param instance the object instance, can be null
     * @param cls the class type
     */
    protected abstract void handleInterfacedMethods(InternalInjector injector, Object instance, Class<?> cls);
}
