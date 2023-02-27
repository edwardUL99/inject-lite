package io.github.edwardUL99.inject.lite.testing.integration;

import io.github.edwardUL99.inject.lite.config.ConfigurationBuilder;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.Client;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.Constants;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.GoodbyeWorldGetter;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.HelloWorldGetter;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.ServiceImpl;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.StringGetter;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.duplicates.DuplicatesClient;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrincipalIntegrationTest {
    private final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
            .withInjectionPackagePrefixes("io.github.edwardUL99.inject.lite.testing.integration.dependencies")
            .withRequireNamedMultipleMatch(false)
            .withSelectFirstDependency(false)
            .withSingleLevelInjection(false);

    @Test
    public void testInjection() {
        try (IntegrationTestContext context = IntegrationTestContext.startIntegrationContext(configurationBuilder)) {
            Injector injector = context.getInjector();
            Client client = injector.inject(Client.class);

            assertNotNull(client);
            assertInstanceOf(ServiceImpl.class, client.service);

            String returned = client.useService();

            assertEquals(returned, String.format("Hello World: Version: %s", ((ServiceImpl) client.service).config.getVersion()));

            Map<String, StringGetter> allDependencies = injector.injectAll(StringGetter.class);

            List<String> foundNames = new ArrayList<>();
            List<StringGetter> foundGetters = new ArrayList<>();

            for (Map.Entry<String, StringGetter> e : allDependencies.entrySet()) {
                foundNames.add(e.getKey());
                foundGetters.add(e.getValue());
            }

            assertTrue(foundNames.contains("helloGetter"));
            assertTrue(foundNames.contains("goodbyeGetter"));

            List<Class<?>> gettersClasses = foundGetters.stream()
                    .map(StringGetter::getClass)
                    .collect(Collectors.toList());

            assertTrue(gettersClasses.contains(HelloWorldGetter.class));
            assertTrue(gettersClasses.contains(GoodbyeWorldGetter.class));

            long value = injector.inject("TEST_VAL", Long.class);

            assertEquals(Constants.TEST_VAL, value);
        }
    }

    @Test
    public void testPrincipalInjection() {
        try (IntegrationTestContext context = IntegrationTestContext.startIntegrationContext(new ConfigurationBuilder()
                .withInjectionPackagePrefixes("io.github.edwardUL99.inject.lite.testing.integration.dependencies")
                .withRequireNamedMultipleMatch(true)
                .withSingleLevelInjection(false))) {
            Injector injector = context.getInjector();

            DuplicatesClient client = injector.inject(DuplicatesClient.class);

            assertEquals("Child 1", client.getChild1().getName());
            assertEquals("Child 2", client.getChild2().getName());
            assertEquals("Child 2", client.getUnknown().getName());
        }
    }
}
