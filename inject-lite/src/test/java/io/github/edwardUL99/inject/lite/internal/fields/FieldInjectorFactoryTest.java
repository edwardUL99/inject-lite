package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.internal.config.Configuration;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

public class FieldInjectorFactoryTest {
    private InternalInjector injector;

    @BeforeEach
    public void init() {
        injector = mock(InternalInjector.class);
    }

    @Test
    public void testGetFieldInjector() {
        FieldInjector fieldInjector = FieldInjectorFactory.getFieldInjector(injector);
        assertInstanceOf(DefaultFieldInjector.class, fieldInjector);

        Configuration.global.setSingleLevelInjection(false);
    }
}
