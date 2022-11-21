package io.github.edwardUL99.inject.lite.internal.injector;

import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjectorFactory;
import io.github.edwardUL99.inject.lite.exceptions.DependencyExistsException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyMismatchException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.exceptions.InvalidInjectableException;
import io.github.edwardUL99.inject.lite.internal.dependency.GraphInjection;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjectorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;

/**
 * Default injector implementation
 */
public class DefaultInjector<D extends InjectableDependency> implements InternalInjector<D> {
    /**
     * Map of injectables in the injection
     */
    protected final Map<String, D> injectables = new ConcurrentSkipListMap<>();
    /**
     * The constructor injector instance
     */
    protected final ConstructorInjector constructorInjector = ConstructorInjectorFactory.getConstructorInjector(this);
    /**
     * The factory for creating dependencies
     */
    protected final InjectableDependencyFactory<D> factory;

    /**
     * Create the dependency with the injectable dependency factory
     * @param factory the factory
     */
    public DefaultInjector(InjectableDependencyFactory<D> factory) {
        this.factory = factory;
    }

    @Override
    public FieldInjector getFieldInjector() {
        return FieldInjectorFactory.getFieldInjector(this);
    }

    @Override
    public ConstructorInjector getConstructorInjector() {
        return constructorInjector;
    }

    @Override
    public <T> void registerDependency(String name, Class<T> cls, boolean singleton) throws DependencyExistsException, InvalidInjectableException {
        if (injectables.containsKey(name)) {
            throw new DependencyExistsException(name);
        } else if (!canInject(cls)) {
            throw new InvalidInjectableException(cls);
        } else {
            D dependency = this.factory.instantiate(name, cls, this, singleton);
            registerInjectableDependency(dependency);
        }
    }

    @Override
    public void registerInjectableDependency(D dependency) {
        injectables.put(dependency.getName(), dependency);
    }

    @Override
    public <T> T inject(String name, Class<T> expected) throws DependencyNotFoundException, DependencyMismatchException {
        return GraphInjection.executeInGraphContext(this, name, expected, () -> injectWithGraph(name, expected));
    }

    @Override
    public <T> T inject(Class<T> type) throws DependencyNotFoundException {
        return injectAll(type).get(0);
    }

    @Override
    public <T> List<T> injectAll(Class<T> type) throws DependencyNotFoundException {
        List<T> found = new ArrayList<>();

        for (Map.Entry<String, D> e : injectables.entrySet()) {
            try {
                found.add(inject(e.getKey(), type));
            } catch (DependencyNotFoundException | DependencyMismatchException ignored) {}
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
            D dependency = injectables.get(name);
            Class<?> cls = dependency.getType();

            if (!expected.isAssignableFrom(cls)) {
                throw new DependencyMismatchException(name, expected, cls);
            } else {
                return (T) dependency.get();
            }
        }
    }

    @Override
    public D getInjectableDependency(Class<?> type) {
        for (D d : injectables.values()) {
            if (type.isAssignableFrom(d.getType())) {
                return d;
            }
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T injectWithGraph(Class<T> type, D dependency) throws DependencyNotFoundException {
        if (dependency == null || !type.isAssignableFrom(dependency.getType())) {
            dependency = getInjectableDependency(type);

            if (dependency != null)
                return (T) dependency.get();
        } else {
            return (T) dependency.get();
        }

        throw new DependencyNotFoundException(type);
    }

    @SuppressWarnings("unchecked")
    private <T> T getInGraphContext(D dependency, Class<T> cls) {
        return GraphInjection.executeInGraphContext(this, dependency.getName(), cls, () -> (T) dependency.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void actOnDependencies(Consumer<T> consumer, Class<T> type) {
        for (D dependency : injectables.values()) {
            Class<?> t = dependency.getType();
            if (type.isAssignableFrom(t)) {
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
            D dependency = factory.instantiate(type.getSimpleName(), type, this, false);

            return getInGraphContext(dependency, type);
        } finally {
            InjectionContext.setSingletonBehaviour(true);
        }
    }
}
