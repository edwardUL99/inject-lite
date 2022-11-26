package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.dependency.Dependency;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraph;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * A base implementation of the field injector. Provides common injection functionality, with the differing
 * functionality being how the fields are looked up
 */
public abstract class BaseFieldInjector implements FieldInjector {
    private final InternalInjector injector;
    private DependencyGraph graph;

    /**
     * The injector to get dependencies with
     * @param injector dependency injection
     */
    public BaseFieldInjector(Injector injector) {
        this.injector = (InternalInjector) injector;
    }

    private String getName(Class<?> cls, Field field) {
        InjectableDependency dependency = injector.getInjectableDependency(cls);

        return (dependency != null) ? dependency.getName() : ((field != null) ? field.getName() : cls.getSimpleName());
    }

    private void setField(Field field, Object resourceInstance, Object obj) {
        Class<?> fieldType = field.getType();
        Class<?> resourceCls = resourceInstance.getClass();

        if (fieldType.isAssignableFrom(resourceCls)) {
            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(obj, resourceInstance);
                field.setAccessible(accessible);
            } catch (IllegalAccessException ex) {
                throw new InjectionException("Failed to set @Inject field", ex);
            }
        } else {
            throw new InjectionException(String.format("@Inject field of type %s not assignable to %s", resourceCls,
                    fieldType));
        }
    }

    private void inject(Inject inject, Field field, Object obj) {
        Object resourceInstance;
        String value = inject.value();
        Class<?> objClass = obj.getClass();
        Class<?> fieldType = field.getType();
        String name = getName(objClass, null);

        if (graph != null) graph.addDependency(new Dependency(name, objClass),
                new Dependency((value.isEmpty()) ? getName(fieldType, field) : value, fieldType));

        if (value.equals("")) {
            resourceInstance = injector.injectWithGraph(fieldType, null);
        } else {
            resourceInstance = injector.injectWithGraph(value, fieldType);
        }

        setField(field, resourceInstance, obj);
    }

    private void doInjection(List<Field> fields, Object obj) {
        for (Field field : fields) {
            Inject inject = field.getAnnotation(Inject.class);

            if (inject != null) {
                if (!Modifier.isFinal(field.getModifiers())) {
                    inject(inject, field, obj);
                } else {
                    throw new InjectionException("Fields annotated with @Inject must not be final");
                }
            }
        }
    }

    @Override
    public final void injectFields(Object obj) {
        Class<?> cls = obj.getClass();
        doInjection(getFields(cls), obj);
    }

    /**
     * Get the list of fields to search through for inject annotated fields. This should simply return all fields in
     * the class. This class will find the Resource annotated fields and inject them
     * @param cls the class of the object being injected with values
     * @return the list of possible fields
     */
    protected abstract List<Field> getFields(Class<?> cls);

    @Override
    public void setDependencyGraph(DependencyGraph graph) {
        this.graph = graph;
    }
}
