package io.github.edwardUL99.inject.lite.testing;

import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjectorFactory;
import io.github.edwardUL99.inject.lite.exceptions.DependencyExistsException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyMismatchException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.exceptions.InvalidInjectableException;
import io.github.edwardUL99.inject.lite.internal.dependency.GraphInjection;
import io.github.edwardUL99.inject.lite.internal.injector.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjectorFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This injector provides injection in a test context
 */
class TestInjector implements InternalInjector<DelayedInjectableDependency> {
    /**
     * Stores test injectables which are checked first by inject
     */
    protected final Map<String, TestDelayedInjectableDependency> testInjectables;
    /**
     * Wrapped injector for normal injection
     */
    protected final InternalInjector<DelayedInjectableDependency> wrappedInjector;
    /**
     * Field injector instance
     */
    protected final FieldInjector fieldInjector = FieldInjectorFactory.getFieldInjector(this);
    /**
     * Constructor injector instance
     */
    protected final ConstructorInjector constructorInjector = ConstructorInjectorFactory.getConstructorInjector(this);

    /**
     * Create an instance
     * @param injector the injector (only supports InternalInjectors)
     */
    public TestInjector(InternalInjector<DelayedInjectableDependency> injector) {
        this(injector, new HashMap<>());
    }

    /**
     * Create an instance
     * @param injector the injector (only supports InternalInjectors)
     * @param testInjectables existing map of test injectables
     */
    public TestInjector(InternalInjector<DelayedInjectableDependency> injector, Map<String, TestDelayedInjectableDependency> testInjectables) {
        this.wrappedInjector = injector;
        this.testInjectables = new HashMap<>(testInjectables);
    }

    /**
     * Registers the test dependency
     * @param name the name of the dependency
     * @param dependency the dependency object
     */
    public void registerTestDependency(String name, Object dependency) {
        registerInjectableDependency(new TestDelayedInjectableDependency(name, dependency));
    }

    @Override
    public ConstructorInjector getConstructorInjector() {
        return constructorInjector;
    }

    @Override
    public FieldInjector getFieldInjector() {
        return fieldInjector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void actOnDependencies(Consumer<T> consumer, Class<T> type) {
        for (TestDelayedInjectableDependency proxy : testInjectables.values()) {
            if (type.isAssignableFrom(proxy.getType()))
                consumer.accept((T)proxy.get());
        }

        wrappedInjector.actOnDependencies(consumer, type);
    }

    @Override
    public void actOnDependencies(Consumer<Object> consumer) {
        actOnDependencies(consumer, Object.class);
    }

    @Override
    public <T> void registerDependency(String name, Class<T> cls, boolean singleton) throws DependencyExistsException,
            InvalidInjectableException {
        wrappedInjector.registerDependency(name, cls, singleton);
    }

    @Override
    public void registerInjectableDependency(DelayedInjectableDependency proxy) {
        if (proxy instanceof TestDelayedInjectableDependency) {
            testInjectables.put(proxy.getName(), (TestDelayedInjectableDependency) proxy);
        } else {
            wrappedInjector.registerInjectableDependency(proxy);
        }
    }

    @Override
    public <T> T inject(String name, Class<T> expected) throws DependencyNotFoundException, DependencyMismatchException {
        return injectWithGraph(name, expected);
    }

    @Override
    public <T> T inject(Class<T> type) throws DependencyNotFoundException {
        return injectWithGraph(type, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T injectWithGraph(String name, Class<T> expected) throws DependencyNotFoundException, DependencyMismatchException {
        DelayedInjectableDependency proxy = testInjectables.get(name);

        if (proxy != null) {
            Class<?> type = proxy.getType();
            if (!expected.isAssignableFrom(type))
                throw new DependencyMismatchException(name, expected, type);

            return (T) proxy.get();
        } else {
            return wrappedInjector.inject(name, expected);
        }
    }

    @Override
    public DelayedInjectableDependency getInjectableDependency(Class<?> type) {
        for (DelayedInjectableDependency d : testInjectables.values()) {
            if (type.isAssignableFrom(d.getType()))
                return d;
        }

        return wrappedInjector.getInjectableDependency(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T injectWithGraph(Class<T> type, DelayedInjectableDependency proxy) throws DependencyNotFoundException {
        if (proxy instanceof TestDelayedInjectableDependency) {
            return (T) proxy.get();
        } else {
            DelayedInjectableDependency p = getInjectableDependency(type);

            return (p != null) ? (T) p.get():wrappedInjector.inject(type);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T instantiate(Class<T> type) throws InjectionException {
        InjectionContext.setSingletonBehaviour(false);
        ConstructorInjector injector = getConstructorInjector();
        FieldInjector fieldInjector = getFieldInjector();

        String name = type.getSimpleName();
        T instantiated = GraphInjection.executeInGraphContext(this, name, type, () -> {
            T instance = (T) injector.injectConstructor(name, type);
            fieldInjector.injectFields(instance);

            return instance;
        });

        InjectionContext.setSingletonBehaviour(true);

        return instantiated;
    }
}
