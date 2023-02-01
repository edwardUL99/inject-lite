package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.annotations.PreConstruct;
import io.github.edwardUL99.inject.lite.exceptions.HookException;
import io.github.edwardUL99.inject.lite.hooks.PreConstructHook;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles pre construct hooks
 */
public class PreConstructHandler extends BaseHookHandler {
    /**
     * Get the method call
     * @param injector the injector instance
     * @param method the method to call
     * @return the runnable
     */
    private Runnable getCallable(InternalInjector injector, Method method) {
        if (!Modifier.isStatic(method.getModifiers()))
            throw new HookException("PreConstruct hook method must be static");

        Parameter[] parameters = method.getParameters();

        if (parameters.length > 1) {
            throw new HookException("PreConstruct hook method must have 0 - 1 arguments");
        } else if (parameters.length == 1) {
            Parameter parameter = parameters[0];

            if (!parameter.getType().equals(Injector.class))
                throw new HookException("PreConstruct hook method argument must be of type Injector");

            return () -> {
                try {
                    method.invoke(null, injector);
                } catch (ReflectiveOperationException ex) {
                    throw new HookException("Failed to call hook preConstruct", ex);
                }
            };
        } else {
            return () -> {
                try {
                    method.invoke(null);
                } catch (ReflectiveOperationException ex) {
                    throw new HookException("Failed to call hook preConstruct", ex);
                }
            };
        }
    }

    @Override
    protected Class<? extends Hook> getHookType() {
        return PreConstructHook.class;
    }

    @Override
    protected Class<? extends Annotation> getAnnotationHook() {
        return PreConstruct.class;
    }

    @Override
    protected void handleAnnotatedMethods(InternalInjector injector, Object instance, List<Method> methods) {
        for (Method m : methods)
            getCallable(injector, m).run();
    }

    private Method getMethod(Class<?> cls) {
        List<Method> methods = Arrays.stream(cls.getDeclaredMethods())
                .filter(m -> m.getName().equals("preConstruct"))
                .collect(Collectors.toList());

        if (methods.size() != 1) {
            throw new HookException("When PreConstruct is implemented, there must be 1 method called " +
                    "preConstruct, with either 0 args or 1 arg of type Injector");
        } else {
            return methods.get(0);
        }
    }

    @Override
    protected void handleInterfacedMethods(InternalInjector injector, Object instance, Class<?> cls) {
        getCallable(injector, getMethod(cls)).run();
    }
}
