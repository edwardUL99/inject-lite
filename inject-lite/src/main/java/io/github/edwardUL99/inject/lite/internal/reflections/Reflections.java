package io.github.edwardUL99.inject.lite.internal.reflections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides reflective scanning operations for the library. This class is in of itself a facade that filters classes
 * returned by the internal facade by configured package prefixes
 */
public class Reflections implements ReflectionsFacade {
    /**
     * The internal reflections facade object wrapping the actual implementation
     */
    private final ReflectionsFacade reflectionsFacade;
    /**
     * List of package prefixes to filter searched classes for
     */
    private final List<String> packagePrefixes;

    /**
     * Create a reflections object with the provided package prefixes
     * @param packagePrefixes the array of prefixes
     * @param reflectionsFacade the underlying reflections facade
     */
    public Reflections(String[] packagePrefixes, ReflectionsFacade reflectionsFacade) {
        this.packagePrefixes = (packagePrefixes == null) ? new ArrayList<>() : Arrays.asList(packagePrefixes);
        this.reflectionsFacade = reflectionsFacade;
    }

    /**
     * Checks if the classPackage is in the list of packagePrefixes
     * @param classPackage the package
     * @return true if matches any package
     */
    private boolean inPackagePrefixes(String classPackage) {
        return packagePrefixes.stream()
                .anyMatch(classPackage::startsWith);
    }

    /**
     * Filter the set of classes by prefixes if there are prefixes, otherwise, just return all of them
     * @param classes the set of classes
     * @return the filtered set
     */
    private Set<Class<?>> filterClassesByPrefixes(Set<Class<?>> classes) {
        return (packagePrefixes.size() == 0) ?
                classes : classes.stream()
                .filter(c -> this.inPackagePrefixes(c.getPackage().getName()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
        return filterClassesByPrefixes(reflectionsFacade.getTypesAnnotatedWith(annotation));
    }
}
