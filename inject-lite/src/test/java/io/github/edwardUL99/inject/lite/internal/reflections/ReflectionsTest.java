package io.github.edwardUL99.inject.lite.internal.reflections;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.internal.reflections.dependencies1.Dependency1;
import io.github.edwardUL99.inject.lite.internal.reflections.dependencies2.Dependency2;
import io.github.edwardUL99.inject.lite.internal.reflections.dependencies3.Dependency3;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReflectionsTest {
    private static Reflections makeReflections(String[] prefixes) {
        ReflectionsFacade mockReflections = mock(ReflectionsFacade.class);
        Reflections reflections = new Reflections(prefixes, mockReflections);

        Set<Class<?>> classes = new LinkedHashSet<>();
        classes.add(Dependency1.class);
        classes.add(Dependency2.class);
        classes.add(Dependency3.class);

        when(mockReflections.getTypesAnnotatedWith(Injectable.class))
                .thenReturn(classes);

        return reflections;
    }

    // using Injectable as a parameter in tests since the annotation doesn't matter as the underlying reflections is mocked

    @Test
    public void testReturnAllClasses() {
        Reflections reflections = makeReflections(new String[]{});

        Set<Class<?>> found = reflections.getTypesAnnotatedWith(Injectable.class);

        assertEquals(3, found.size());
        assertTrue(found.contains(Dependency1.class));
        assertTrue(found.contains(Dependency2.class));
        assertTrue(found.contains(Dependency3.class));
    }

    @Test
    public void testReturnPrefixedClasses() {
        Reflections reflections = makeReflections(new String[]{
                "io.github.edwardUL99.inject.lite.internal.reflections.dependencies1",
                "io.github.edwardUL99.inject.lite.internal.reflections.dependencies2"
        });

        Set<Class<?>> found = reflections.getTypesAnnotatedWith(Injectable.class);

        assertEquals(2, found.size());
        assertTrue(found.contains(Dependency1.class));
        assertTrue(found.contains(Dependency2.class));
        assertFalse(found.contains(Dependency3.class));
    }
}
