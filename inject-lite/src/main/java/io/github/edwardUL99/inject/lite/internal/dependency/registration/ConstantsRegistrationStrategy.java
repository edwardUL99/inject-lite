package io.github.edwardUL99.inject.lite.internal.dependency.registration;

import io.github.edwardUL99.inject.lite.annotations.Constant;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.injector.Injector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A registration strategy for registering constant fields
 */
public class ConstantsRegistrationStrategy implements RegistrationStrategy {
    /**
     * The class containing the constants annotated fields
     */
    private final Class<?> cls;

    /**
     * Create the strategy for the provided class
     * @param cls the class containing the constant annotated fields
     */
    public ConstantsRegistrationStrategy(Class<?> cls) {
        this.cls = cls;
    }

    private void registerConstantField(Constant constant, Injector injector, Field field) {
        int modifiers = field.getModifiers();

        if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
            try {
                String name = constant.value();
                name = (name.isEmpty()) ? field.getName() : name;

                injector.registerConstantDependency(name, field.getType(), field.get(null));
            } catch (IllegalAccessException ex) {
                throw new InjectionException("Failed to register constant field", ex);
            }
        } else {
            throw new InjectionException("Field " + field.getName() + " must be public, static and final");
        }
    }

    @Override
    public void register(Injector injector) {
        for (Field field : cls.getDeclaredFields()) {
            Constant constant = field.getAnnotation(Constant.class);

            if (constant != null)
                registerConstantField(constant, injector, field);
        }
    }
}
