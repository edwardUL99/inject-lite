package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.injector.Injector;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of a resource injector which only injects fields in the class only and not the inheritance hierarchy
 */
public class SingleLevelFieldInjector extends BaseFieldInjector {
    public SingleLevelFieldInjector(Injector injector) {
        super(injector);
    }

    @Override
    protected List<Field> getFields(Class<?> cls) {
        return Arrays.asList(cls.getDeclaredFields());
    }
}
