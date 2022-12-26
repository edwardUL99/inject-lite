package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Lazy;
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

    private InjectableDependency getInjectableDependency(Class<?> cls, Field field) {
        Class<?> type = (field == null) ? cls : field.getType();

        return dependencyHandler.getInjectableDependency(type, () -> (field == null) ? null : field.getName());
    }

    private String getName(Class<?> cls, Field field, InjectableDependency dependency) {
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

    private Object injectDependencyInstance(String value, Class<?> fieldType, Field field, DependencyGraph graph, String name,
                                            InjectableDependency targetDependency, Class<?> objClass) {
        Object instance;

        addToGraph(graph, new Dependency(name, objClass),
                new Dependency((value.isEmpty()) ? getName(fieldType, field, targetDependency) : value, fieldType),
                targetDependency);

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

    private Object getDependencyInstance(String value, Class<?> fieldType, Field field, DependencyGraph graph, String name,
                                         InjectableDependency targetDependency, Class<?> objClass) {
        return dependencyHandler.getDependencyCheckingLazy(
                field.getAnnotation(Lazy.class),
                fieldType,
                () -> injectDependencyInstance(value, fieldType, field, graph, name, targetDependency, objClass)
        );
    }

    // only adds to graph if the dependency needs to be instantiated
    private void addToGraph(DependencyGraph graph, Dependency sourceDependency, Dependency targetDependency,
                            InjectableDependency injectableDependency) {
        if (graph != null) {
            if (injectableDependency == null || !injectableDependency.isInstantiated()) {
                graph.addDependency(sourceDependency, targetDependency);
            }
        }
    }

    private void inject(Inject inject, Field field, Object obj) {
        String value = inject.value();
        Class<?> objClass = obj.getClass();
        Class<?> fieldType = field.getType();
        InjectableDependency objDependency = getInjectableDependency(objClass, null);
        String name = getName(objClass, null, objDependency);
        InjectableDependency targetDependency = getInjectableDependency(fieldType, field);

        setField(field, getDependencyInstance(value, fieldType, field, graph, name, targetDependency, objClass), obj);
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
