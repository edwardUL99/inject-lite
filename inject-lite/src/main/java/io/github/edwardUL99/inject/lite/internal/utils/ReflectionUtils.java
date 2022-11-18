package io.github.edwardUL99.inject.lite.internal.utils;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Utilities for reflection
 */
public final class ReflectionUtils {
    /**
     * Shared reflections instance
     */
    private static Reflections reflections;
    /**
     * The prefixes for packages to search using reflection
     */
    private static String[] prefixes;

    private ReflectionUtils() {}

    /**
     * Set the reflections prefixes
     * @param prefixes prefixes to search for classes
     */
    public static void setReflectionsPrefixes(String[] prefixes) {
        ReflectionUtils.prefixes = prefixes;
    }

    /**
     * Get a shared single instance reflections object that scans the Java classpath
     * @return the shared reflections instance
     */
    public static Reflections getReflections() {
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
}
