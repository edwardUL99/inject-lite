package io.github.edwardUL99.inject.lite.internal.utils;

import io.github.edwardUL99.inject.lite.config.Configuration;
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

    private ReflectionUtils() {}

    /**
     * Get a shared single instance reflections object that scans the Java classpath
     * @return the shared reflections instance
     */
    public static Reflections getReflections() {
        String[] prefixes = Configuration.getInjectionPackagePrefixes();

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
