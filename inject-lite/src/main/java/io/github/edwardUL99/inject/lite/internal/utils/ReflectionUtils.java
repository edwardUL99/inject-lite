package io.github.edwardUL99.inject.lite.internal.utils;

import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.reflections.Reflections;
import io.github.edwardUL99.inject.lite.internal.reflections.ReflectionsLibraryFacade;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for reflection
 */
public final class ReflectionUtils {
    /**
     * Shared reflections instance
     */
    private static Reflections reflections;
    /**
     * Map of primitives to wrappers
     */
    private static final Map<Class<?>, Class<?>> primitiveWrappers = new HashMap<>();
    /**
     * Default values for primitives
     */
    private static final Map<Class<?>, Object> defaultValues = new HashMap<>();

    static {
        addWrapperMapping(int.class, Integer.class);
        addWrapperMapping(float.class, Float.class);
        addWrapperMapping(double.class, Double.class);
        addWrapperMapping(boolean.class, Boolean.class);
        addWrapperMapping(byte.class, Byte.class);
        addWrapperMapping(char.class, Character.class);
        addWrapperMapping(short.class, Short.class);
        addWrapperMapping(long.class, Long.class);

        defaultValues.put(int.class, 0);
        defaultValues.put(float.class, 0.00F);
        defaultValues.put(double.class, 0.00);
        defaultValues.put(boolean.class, false);
        defaultValues.put(byte.class, '\0');
        defaultValues.put(char.class, '\0');
        defaultValues.put(short.class, 0);
        defaultValues.put(long.class, 0L);
    }

    /**
     * Adds a primitive wrapper mapping
     * @param primitive the primitive class
     * @param wrapper the wrapper class
     */
    private static void addWrapperMapping(Class<?> primitive, Class<?> wrapper) {
        primitiveWrappers.put(primitive, wrapper);
        primitiveWrappers.put(wrapper, primitive);
    }

    private ReflectionUtils() {}

    /**
     * Get a shared single instance reflections object that scans the Java classpath
     * @return the shared reflections instance
     */
    public static Reflections getReflections() {
        if (reflections == null) {
            reflections = new Reflections(Configuration.global.getInjectionPackagePrefixes(),
                    new ReflectionsLibraryFacade());
        }

        return reflections;
    }

    /**
     * Determines if subType can be assigned to superType
     * @param superType the type to determine if subtype can be assigned to
     * @param subType the type to check if it can be assigned to superType
     * @return true if assignable, false if not
     */
    public static boolean isAssignable(Class<?> superType, Class<?> subType) {
        // Boolean field - boolean injectable
        Class<?> wrapperSuperType = primitiveWrappers.get(superType);
        Class<?> wrapperSubType = primitiveWrappers.get(subType);

        if (superType.isPrimitive() && !subType.isPrimitive()) {
            if (wrapperSubType != null) {
                return superType.isAssignableFrom(wrapperSubType);
            }
        } else if (!superType.isPrimitive() && subType.isPrimitive()) {
            if (wrapperSuperType != null) {
                return wrapperSuperType.isAssignableFrom(subType);
            }
        }

        return superType.isAssignableFrom(subType);
    }

    /**
     * Get the default value to use for the provided class
     * @param cls the class object
     * @return the default value
     */
    public static Object getDefaultValue(Class<?> cls) {
        return defaultValues.get(cls);
    }
}
