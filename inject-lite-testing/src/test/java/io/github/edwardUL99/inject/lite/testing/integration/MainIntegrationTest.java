package io.github.edwardUL99.inject.lite.testing.integration;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.Client;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.GoodbyeWorldGetter;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.HelloWorldGetter;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.ServiceImpl;
import io.github.edwardUL99.inject.lite.testing.integration.dependencies.StringGetter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("IntegrationTest")
public class MainIntegrationTest {
    private Injector injector;

    @BeforeAll
    public static void staticInit() {
//        Injection.configure(new ConfigurationBuilder()
//                .withInjectionPackagePrefixes("io.github.edwardUL99.inject.lite.testing.integration.dependencies")
//                .withRequireNamedMultipleMatch(false)
//                .withSelectFirstDependency(false)
//                .withSingleLevelInjection(false));
    }

    @BeforeEach
    public void init() {
        injector = Injector.get();
    }

    @AfterEach
    public void teardown() {
        Injection.resetGlobalInjector();
    }

    @Test
    public void testInjection() {
        Client client = injector.inject(Client.class);

        assertNotNull(client);
        assertInstanceOf(ServiceImpl.class, client.service);

        String returned = client.useService();

        assertEquals(returned, String.format("Hello World: Version: %s", ((ServiceImpl)client.service).config.getVersion()));

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
    }
}
