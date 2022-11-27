package io.github.edwardUL99.inject.lite.internal.utils;

import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

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

    static {
        addWrapperMapping(int.class, Integer.class);
        addWrapperMapping(float.class, Float.class);
        addWrapperMapping(double.class, Double.class);
        addWrapperMapping(boolean.class, Boolean.class);
        addWrapperMapping(byte.class, Byte.class);
        addWrapperMapping(char.class, Character.class);
        addWrapperMapping(short.class, Short.class);
        addWrapperMapping(long.class, Long.class);
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
        String[] prefixes = Configuration.global.getInjectionPackagePrefixes();

        if (reflections == null) {
            if (prefixes != null) {
                reflections = new Reflections(new ConfigurationBuilder().forPackages(prefixes));
            } else {
                reflections = new Reflections(new ConfigurationBuilder()
                        .addUrls(ClasspathHelper.forJavaClassPath()));
            }
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
}
