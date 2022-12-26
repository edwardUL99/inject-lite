package io.github.edwardUL99.inject.lite.testing;

import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjector;
import io.github.edwardUL99.inject.lite.internal.constructors.ConstructorInjectorFactory;
import io.github.edwardUL99.inject.lite.exceptions.DependencyExistsException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyMismatchException;
import io.github.edwardUL99.inject.lite.exceptions.DependencyNotFoundException;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.exceptions.InvalidInjectableException;
import io.github.edwardUL99.inject.lite.internal.dependency.graph.GraphInjection;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjector;
import io.github.edwardUL99.inject.lite.internal.fields.FieldInjectorFactory;
import io.github.edwardUL99.inject.lite.internal.methods.MethodInjector;
import io.github.edwardUL99.inject.lite.internal.methods.MethodInjectorFactory;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This injector provides injection in a test context
 */
class TestInjector implements InternalInjector {
    /**
     * Stores test injectables which are checked first by inject
     */
    protected final Map<String, TestDelayedInjectableDependency> testInjectables;
    /**
     * Wrapped injector for normal injection
     */
    protected final InternalInjector wrappedInjector;
    /**
     * Field injector instance
     */
    protected final FieldInjector fieldInjector = FieldInjectorFactory.getFieldInjector(this);
    /**
     * Constructor injector instance
     */
    protected final ConstructorInjector constructorInjector = ConstructorInjectorFactory.getConstructorInjector(this);
    /**
     * The method injector instance
     */
    protected final MethodInjector methodInjector = MethodInjectorFactory.getMethodInjector(this);

    /**
     * Create an instance
     * @param injector the injector (only supports InternalInjectors)
     */
    public TestInjector(InternalInjector injector) {
        this(injector, new HashMap<>());
    }

    /**
     * Create an instance
     * @param injector the injector (only supports InternalInjectors)
     * @param testInjectables existing map of test injectables
     */
    public TestInjector(InternalInjector injector, Map<String, TestDelayedInjectableDependency> testInjectables) {
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
    public void registerConstantDependency(String name, Class<?> type, Object value) throws DependencyExistsException {
        wrappedInjector.registerConstantDependency(name, type, value);
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
    public MethodInjector getMethodInjector() {
        return methodInjector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void actOnDependencies(Consumer<T> consumer, Class<T> type) {
        for (TestDelayedInjectableDependency proxy : testInjectables.values()) {
            if (ReflectionUtils.isAssignable(type, proxy.getType()))
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
    public void registerInjectableDependency(InjectableDependency proxy) {
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
        return new ArrayList<>(injectAll(type).values()).get(0);
    }

    @Override
    public <T> Map<String, T> injectAll(Class<T> type) throws DependencyNotFoundException {
        Map<String, T> found = new LinkedHashMap<>();

        for (Map.Entry<String, TestDelayedInjectableDependency> e : testInjectables.entrySet()) {
            try {
                String name = e.getKey();
                T dependency = injectWithGraph(name, type, false);
                if (dependency != null) found.put(name, dependency);
            } catch (DependencyMismatchException ignored) {}
        }

        try {
            found.putAll(wrappedInjector.injectAll(type));
        } catch (DependencyNotFoundException exception) {
            if (found.size() == 0) throw new DependencyNotFoundException(type);
        }

        return found;
    }

    @Override
    public <T> T injectWithGraph(String name, Class<T> expected) throws DependencyNotFoundException, DependencyMismatchException {
        return injectWithGraph(name, expected, true);
    }

    @SuppressWarnings("unchecked")
    private <T> T injectWithGraph(String name, Class<T> expected, boolean useWrapped) throws DependencyNotFoundException, DependencyMismatchException {
        InjectableDependency dependency = testInjectables.get(name);

        if (dependency != null) {
            Class<?> type = dependency.getType();
            if (!ReflectionUtils.isAssignable(expected, type))
                throw new DependencyMismatchException(name, expected, type);

            return (T) dependency.get();
        } else if (useWrapped) {
            return wrappedInjector.inject(name, expected);
        }

        return null;
    }

    @Override
    public InjectableDependency getInjectableDependency(String name, Class<?> expected) throws DependencyMismatchException {
        InjectableDependency dependency = testInjectables.get(name);

        if (dependency != null) {
            Class<?> type;

            if (ReflectionUtils.isAssignable(expected, (type = dependency.getType())))
                throw new DependencyMismatchException(name, expected, type);

            return dependency;
        } else {
            return wrappedInjector.getInjectableDependency(name, expected);
        }
    }

    @Override
    public List<InjectableDependency> getInjectableDependencies(Class<?> type) {
        List<InjectableDependency> dependencies = testInjectables.values()
                .stream()
                .filter(d -> ReflectionUtils.isAssignable(type, d.getType())).collect(Collectors.toCollection(ArrayList::new));

        dependencies.addAll(wrappedInjector.getInjectableDependencies(type));

        return dependencies;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T injectWithGraph(Class<T> type, InjectableDependency dependency) throws DependencyNotFoundException {
        if (dependency instanceof TestDelayedInjectableDependency) {
            return (T) dependency.get();
        } else {
            InjectableDependency p = getInjectableDependency(type);

            return (p != null) ? (T) p.get():wrappedInjector.inject(type);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T instantiate(Class<T> type) throws InjectionException {
        T instantiated;

        try {
            InjectionContext.setSingletonBehaviour(false);
            InjectionContext.setLazyBehaviourDisabled(true);
            ConstructorInjector injector = getConstructorInjector();
            FieldInjector fieldInjector = getFieldInjector();

            String name = type.getSimpleName();
            instantiated = GraphInjection.executeInGraphContext(this, name, type, () -> {
                T instance = (T) injector.injectConstructor(name, type);
                fieldInjector.injectFields(instance);

                return instance;
            });
        } finally {
            InjectionContext.setSingletonBehaviour(true);
            InjectionContext.setLazyBehaviourDisabled(false);
        }

        return instantiated;
    }
}
