package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjectorFactory;
import io.github.edwardUL99.inject.lite.exceptions.DependencyExistsException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyMismatchException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.exceptions.InvalidInjectableException;
import io.github.edwardUL99.inject.lite.internal.dependency.ConstantInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependencyFactory;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.GraphInjection;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjectorFactory;
import io.github.edwardUL99.inject.lite.internal.hooks.InjectorHooks;
import io.github.edwardUL99.inject.lite.internal.methods.MethodInjector;
import io.github.edwardUL99.inject.lite.internal.methods.MethodInjectorFactory;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Default injector implementation
 */
public class DefaultInjector implements InjectorHooks.HookSupport {
    /**
     * Map of injectables in the injection
     */
    protected Map<String, InjectableDependency> injectables = new ConcurrentSkipListMap<>();
    /**
     * The field injector instance
     */
    protected final FieldInjector fieldInjector = FieldInjectorFactory.getFieldInjector(this);
    /**
     * The constructor injector instance
     */
    protected final ConstructorInjector constructorInjector = ConstructorInjectorFactory.getConstructorInjector(this);
    /**
     * The method injector instance
     */
    protected final MethodInjector methodInjector = MethodInjectorFactory.getMethodInjector(this);
    /**
     * The factory for creating dependencies
     */
    protected final InjectableDependencyFactory factory;

    /**
     * Create the dependency with the injectable dependency factory
     * @param factory the factory
     */
    public DefaultInjector(InjectableDependencyFactory factory) {
        this.factory = factory;
    }

    /**
     * Test method to set injectables map
     * @param injectables injectables map
     */
    void setInjectablesForTest(Map<String, InjectableDependency> injectables) {
        this.injectables = injectables;
    }

    @Override
    public FieldInjector getFieldInjector() {
        return fieldInjector;
    }

    @Override
    public ConstructorInjector getConstructorInjector() {
        return constructorInjector;
    }

    @Override
    public MethodInjector getMethodInjector() {
        return methodInjector;
    }

    @Override
    public <T> void registerDependency(String name, Class<T> cls, boolean singleton) throws DependencyExistsException, InvalidInjectableException {
        if (injectables.containsKey(name)) {
            throw new DependencyExistsException(name);
        } else if (!canInject(cls)) {
            throw new InvalidInjectableException(cls);
        } else {
            InjectableDependency dependency = this.factory.instantiate(name, cls, this, singleton);
            registerInjectableDependency(dependency);
        }
    }

    @Override
    public void registerConstantDependency(String name, Class<?> type, Object value) throws DependencyExistsException {
        if (injectables.containsKey(name)) {
            throw new DependencyExistsException(name);
        } else {
            registerInjectableDependency(new ConstantInjectableDependency(name, type, this, value));
        }
    }

    @Override
    public void registerInjectableDependency(InjectableDependency dependency) {
        injectables.put(dependency.getName(), dependency);
    }

    @Override
    public <T> T inject(String name, Class<T> expected) throws DependencyNotFoundException, DependencyMismatchException {
        return GraphInjection.executeInGraphContext(this, name, expected, () -> injectWithGraph(name, expected));
    }

    @Override
    public <T> T inject(Class<T> type) throws DependencyNotFoundException {
        return new ArrayList<>(injectAll(type).values()).get(0);
    }

    @Override
    public <T> Map<String, T> injectAll(Class<T> type) throws DependencyNotFoundException {
        Map<String, T> found = new LinkedHashMap<>();

        for (Map.Entry<String, InjectableDependency> e : injectables.entrySet()) {
            try {
                String name = e.getKey();
                found.put(name, inject(name, type));
            } catch (DependencyMismatchException ignored) {
            }
        }

        if (found.size() == 0) throw new DependencyNotFoundException(type);

        return found;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T injectWithGraph(String name, Class<T> expected) throws DependencyNotFoundException, DependencyMismatchException {
        if (!injectables.containsKey(name)) {
            throw new DependencyNotFoundException(name);
        } else {
            InjectableDependency dependency = injectables.get(name);
            Class<?> cls = dependency.getType();

            if (!ReflectionUtils.isAssignable(expected, cls)) {
                throw new DependencyMismatchException(name, expected, cls);
            } else {
                return (T) dependency.get();
            }
        }
    }

    @Override
    public InjectableDependency getInjectableDependency(String name, Class<?> expected) throws DependencyMismatchException {
        InjectableDependency dependency = injectables.get(name);

        Class<?> type;
        if (dependency != null && !ReflectionUtils.isAssignable(expected, (type = dependency.getType())))
            throw new DependencyMismatchException(name, expected, type);

        return dependency;
    }

    @Override
    public List<InjectableDependency> getInjectableDependencies(Class<?> type) {
        return injectables.values()
                .stream()
                .filter(d -> ReflectionUtils.isAssignable(type, d.getType()))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T injectWithGraph(Class<T> type, InjectableDependency dependency) throws DependencyNotFoundException {
        if (dependency == null || !ReflectionUtils.isAssignable(type, dependency.getType())) {
            dependency = getInjectableDependency(type);

            if (dependency != null)
                return (T) dependency.get();
        } else {
            return (T) dependency.get();
        }

        throw new DependencyNotFoundException(type);
    }

    @SuppressWarnings("unchecked")
    private <T> T getInGraphContext(InjectableDependency dependency, Class<T> cls) {
        return GraphInjection.executeInGraphContext(this, dependency.getName(), cls, () -> (T) dependency.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void actOnDependencies(Consumer<T> consumer, Class<T> type) {
        for (InjectableDependency dependency : injectables.values()) {
            Class<?> t = dependency.getType();
            if (ReflectionUtils.isAssignable(type, t)) {
                consumer.accept(getInGraphContext(dependency, (Class<T>) t));
            }
        }
    }

    @Override
    public void actOnDependencies(Consumer<Object> consumer) {
        actOnDependencies(consumer, Object.class);
    }

    @Override
    public <T> T instantiate(Class<T> type) throws InjectionException {
        try {
            InjectionContext.setSingletonBehaviour(false);
            InjectionContext.setLazyBehaviourDisabled(true);
            InjectableDependency dependency = factory.instantiate(type.getSimpleName(), type, this, false);

            return getInGraphContext(dependency, type);
        } finally {
            InjectionContext.setSingletonBehaviour(true);
            InjectionContext.setLazyBehaviourDisabled(false);
        }
    }
}
