package io.github.edwardUL99.inject.lite.internal.reflections;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * This class is a facade around the org.reflections library
 */
public class ReflectionsLibraryFacade implements ReflectionsFacade {
    /**
     * The wrapped reflections library object
     */
    private final Reflections wrapped = new Reflections(ClasspathHelper.forJavaClassPath());

    @Override
    public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
        return wrapped.getTypesAnnotatedWith(annotation);
    }
}
