package io.github.edwardUL99.inject.lite.internal.reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * This interface represents the API to wrap around an implementation/library which provides
 * reflective operations, e.g. searching for annotated types. The current implementation used
 * is the org.reflections library. Only contains the API of interest to the inject-lite library and
 * can wrap around any implementation
 */
public interface ReflectionsFacade {
    /**
     * Get all types annotated with the provided annotation
     * @param annotation the annotation
     * @return the Set of annotated types filtered by annotation prefixes
     */
    Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation);
}
