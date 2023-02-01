package io.github.edwardUL99.inject.lite.internal.hooks;

import io.github.edwardUL99.inject.lite.annotations.Constructed;
import io.github.edwardUL99.inject.lite.exceptions.HookException;
import io.github.edwardUL99.inject.lite.hooks.ConstructedHook;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Handles post construction hooks
 */
public class ConstructedHandler extends BaseHookHandler {
    @Override
    protected Class<? extends Hook> getHookType() {
        return ConstructedHook.class;
    }

    @Override
    protected Class<? extends Annotation> getAnnotationHook() {
        return Constructed.class;
    }

    @Override
    protected String validateAnnotationHookParameters(Method method) {
        Parameter[] parameters = method.getParameters();

        if (parameters.length != 1) {
            return "Must have 1 parameter";
        } else if (!parameters[0].getType().equals(Injector.class)) {
            return "Parameter must be of type Injector";
        } else {
            return null;
        }
    }

    @Override
    protected void handleAnnotatedMethods(InternalInjector injector, Object instance, List<Method> methods) {
        try {
            for (Method m : methods)
                m.invoke(instance, injector);
        } catch (ReflectiveOperationException ex) {
            throw new HookException("Failed to execute ConstructedHook method", ex);
        }
    }

    @Override
    protected void handleInterfacedMethods(InternalInjector injector, Object instance, Class<?> cls) {
        ((ConstructedHook)instance).constructed(injector);
    }
}
