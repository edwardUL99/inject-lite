package io.github.edwardUL99.inject.lite.testing;

import io.github.edwardUL99.inject.lite.internal.injector.DefaultInjectorFactory;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.injector.DelayedInjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InjectorFactory;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.util.Map;

/**
 * Creates instances of test injectors
 */
public class TestInjectorFactory implements InjectorFactory {
    /**
     * Wrapped factory for creating injectors for creating real dependencies
     */
    private final InjectorFactory injectorFactory;

    /**
     * Create the factory with the default factory
     */
    public TestInjectorFactory() {
        this(new DefaultInjectorFactory());
    }

    /**
     * Create the factory wrapping the factory required to create wrapped injectors
     * @param wrappedFactory the factory for internal wrapped injectors
     */
    public TestInjectorFactory(InjectorFactory wrappedFactory) {
        this.injectorFactory = wrappedFactory;
    }

    private TestInjector getExisting() {
        for (Map.Entry<Thread, Injector> e : InjectionContext.getThreadInjectors().entrySet()) {
            Injector injector = e.getValue();

            if (injector instanceof TestInjector)
                return (TestInjector) injector;
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Injector create() {
        TestInjector existing = getExisting();
        if (existing != null) {
            return new TestInjector((InternalInjector<DelayedInjectableDependency>) injectorFactory.create(), existing.testInjectables);
        } else {
            return new TestInjector((InternalInjector<DelayedInjectableDependency>) injectorFactory.create());
        }
    }
}
