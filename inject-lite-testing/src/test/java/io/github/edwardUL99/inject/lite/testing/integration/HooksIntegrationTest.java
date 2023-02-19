package io.github.edwardUL99.inject.lite.testing.integration;

import io.github.edwardUL99.inject.lite.config.ConfigurationBuilder;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.testing.integration.hooks.DependencyAnnotations;
import io.github.edwardUL99.inject.lite.testing.integration.hooks.DependencyInterface;
import io.github.edwardUL99.inject.lite.testing.integration.hooks.LazyClient;
import io.github.edwardUL99.inject.lite.testing.integration.hooks.LazyDependency;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HooksIntegrationTest {
    private final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
            .withInjectionPackagePrefixes("io.github.edwardUL99.inject.lite.testing.integration.hooks")
            .withSingleLevelInjection(false);

    @Test
    public void testInterfaceHooks() {
        try (IntegrationTestContext context = IntegrationTestContext.startIntegrationContext(configurationBuilder)) {
            Injector injector = context.getInjector();

            assertNull(DependencyInterface.preConstructed);

            DependencyInterface dependency = injector.inject(DependencyInterface.class);

            assertSame(injector, DependencyInterface.preConstructed);
            assertSame(injector, dependency.constructed);
        }
    }

    @Test
    public void testAnnotationsHooks() {
        try (IntegrationTestContext context = IntegrationTestContext.startIntegrationContext(configurationBuilder)) {
            Injector injector = context.getInjector();

            assertNull(DependencyAnnotations.preConstructed);
            assertNull(DependencyAnnotations.preConstructed1);

            DependencyAnnotations dependency = injector.inject(DependencyAnnotations.class);

            assertSame(injector, DependencyAnnotations.preConstructed);
            assertSame(injector, DependencyAnnotations.preConstructed1);
            assertSame(injector, dependency.constructed);
            assertSame(injector, dependency.constructed1);
        }
    }

    @Test
    public void testLazyHook() throws Exception {
        try (IntegrationTestContext context = IntegrationTestContext.startIntegrationContext(configurationBuilder)) {
            Injector injector = context.getInjector();

            LazyDependency dependency = injector.inject(LazyClient.class).lazyDependency;

            assertFalse(LazyDependency.targetCalled);
            assertNull(LazyDependency.lazilyInvoked);
            assertNull(LazyDependency.lazilyInvokedInjector);

            dependency.target();

            assertTrue(LazyDependency.targetCalled);
            assertEquals(LazyDependency.lazilyInvoked, LazyDependency.class.getDeclaredMethod("target"));
            assertSame(injector, LazyDependency.lazilyInvokedInjector);
        }
    }
}
