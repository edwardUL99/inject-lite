package io.github.edwardUL99.inject.lite.internal.reflections;

import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import org.reflections.util.ClasspathHelper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A wrapper of the org.reflections package
 */
public class Reflections {
    /**
     * The internal reflections object
     */
    private static final org.reflections.Reflections reflections
            = new org.reflections.Reflections(ClasspathHelper.forJavaClassPath());

    /**
     * Checks if the classPackage is in the list of packagePrefixes
     * @param classPackage the package
     * @param packagePrefixes the prefixes
     * @return true if matches any package
     */
    private boolean inPackagePrefixes(String classPackage, List<String> packagePrefixes) {
        return packagePrefixes.size() == 0 || packagePrefixes.stream()
                .anyMatch(classPackage::startsWith);
    }

    /**
     * Get all types annotated with the provided annotation
     * @param annotation the annotation
     * @return the Set of annotated types filtered by annotation prefixes
     */
    public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
        String[] prefixesArray = Configuration.global.getInjectionPackagePrefixes();
        List<String> prefixes = (prefixesArray == null) ? new ArrayList<>() : Arrays.asList(prefixesArray);

        return reflections.getTypesAnnotatedWith(annotation)
                .stream()
                .filter(c -> this.inPackagePrefixes(c.getPackage().getName(), prefixes))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
