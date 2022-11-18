package io.github.edwardUL99.inject.lite.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class TestUtils {
    public static void setInternalStaticField(Class<?> cls, String field, Object value) {
        try {
            Field f = cls.getDeclaredField(field);
            f.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            f.set(null, value);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
