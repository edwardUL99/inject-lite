package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Optional;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.dependency.CommonDependencyHandler;
import io.github.edwardUL99.inject.lite.internal.dependency.Dependency;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.DependencyGraph;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A default  of the field injector
 */
public class DefaultFieldInjector implements FieldInjector {
    private final InternalInjector injector;
    private CommonDependencyHandler dependencyHandler;
    private DependencyGraph graph;

    /**
     * The injector to get dependencies with
     * @param injector dependency injection
     */
    public DefaultFieldInjector(Injector injector) {
        this.injector = (InternalInjector) injector;
        this.dependencyHandler = new CommonDependencyHandler(this.injector);
    }

    // used to allow injection of mock handlers in testing
    void setDependencyHandler(CommonDependencyHandler dependencyHandler) {
        this.dependencyHandler = dependencyHandler;
    }

    private String getName(Class<?> cls, Field field) {
        Class<?> type = (field == null) ? cls : field.getType();
        InjectableDependency dependency = dependencyHandler.getInjectableDependency(type,
                () -> (field == null) ? null : field.getName());

        return (dependency != null) ? dependency.getName() : ((field != null) ? field.getName() : cls.getSimpleName());
    }

    private void setField(Field field, Object resourceInstance, Object obj) {
        Class<?> fieldType = field.getType();
        Class<?> resourceCls = (resourceInstance == null) ? null : resourceInstance.getClass();

        if (resourceCls == null || ReflectionUtils.isAssignable(fieldType, resourceCls)) {
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

    private Object getDependencyInstance(String value, Class<?> fieldType, Field field) {
        Object instance;

        try {
            if (value.equals("")) {
                instance = injector.injectWithGraph(fieldType, null);
            } else {
                instance = injector.injectWithGraph(value, fieldType);
            }
        } catch (DependencyNotFoundException ex) {
            if (field.getAnnotation(Optional.class) != null)
                instance = ReflectionUtils.getDefaultValue(fieldType);
            else
                throw ex;
        }

        return instance;
    }

    private void inject(Inject inject, Field field, Object obj) {
        String value = inject.value();
        Class<?> objClass = obj.getClass();
        Class<?> fieldType = field.getType();
        String name = getName(objClass, null);

        if (graph != null) graph.addDependency(new Dependency(name, objClass),
                new Dependency((value.isEmpty()) ? getName(fieldType, field) : value, fieldType));

        setField(field, getDependencyInstance(value, fieldType, field), obj);
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

    private void recurseSearchFields(Class<?> cls, List<Field> fields) {
        if (cls != null) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));

            if (!Configuration.global.isSingleLevelInjection())
                recurseSearchFields(cls.getSuperclass(), fields);
        }
    }

    private List<Field> getFields(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        recurseSearchFields(cls, fields);

        return fields;
    }

    @Override
    public void setDependencyGraph(DependencyGraph graph) {
        this.graph = graph;
    }
}
