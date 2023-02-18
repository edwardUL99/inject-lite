package io.github.edwardUL99.inject.lite.testing.integration;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.config.ConfigurationBuilder;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.config.InternalConfig;
import io.github.edwardUL99.inject.lite.internal.utils.ReflectionUtils;

public class IntegrationTestContext implements AutoCloseable {
    private IntegrationTestContext() {}

    public Injector getInjector() {
        return Injector.get();
    }

    private static void reset() {
        ReflectionUtils.resetReflections();
        InternalConfig.setDisableConfiguredCheck(false);
        Injection.resetGlobalInjector();
        InternalConfig.configure(new Configuration());
    }

    @Override
    public void close() {
        reset();
        InternalConfig.setDisableConfiguredCheck(true);
    }

    public static IntegrationTestContext startIntegrationContext(ConfigurationBuilder builder) {
        reset();
        InternalConfig.configure(builder.build());

        return new IntegrationTestContext();
    }
}
