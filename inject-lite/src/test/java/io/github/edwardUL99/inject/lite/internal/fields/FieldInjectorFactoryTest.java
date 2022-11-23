package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.edwardUL99.inject.lite.utils.TestUtils.setInternalStaticField;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

public class FieldInjectorFactoryTest {
    private InternalInjector<?> injector;

    @BeforeEach
    public void init() {
        injector = mock(InternalInjector.class);
    }

    @Test
    public void testMultiLevelInjector() {
        FieldInjector fieldInjector = FieldInjectorFactory.multiLevelInjector(injector);
        assertInstanceOf(MultiLevelFieldInjector.class, fieldInjector);
    }

    @Test
    public void testSingleLevelInjector() {
        FieldInjector fieldInjector = FieldInjectorFactory.singleLevelInjector(injector);
        assertInstanceOf(SingleLevelFieldInjector.class, fieldInjector);
    }

    @Test
    public void testGetFieldInjector() {
        setInternalStaticField(FieldInjectorFactory.class, "singleLevelInjection", false);
        FieldInjector fieldInjector = FieldInjectorFactory.getFieldInjector(injector);
        assertInstanceOf(MultiLevelFieldInjector.class, fieldInjector);

        setInternalStaticField(FieldInjectorFactory.class, "singleLevelInjection", true);
        fieldInjector = FieldInjectorFactory.singleLevelInjector(injector);
        assertInstanceOf(SingleLevelFieldInjector.class, fieldInjector);
    }
    @Test
    public void testGetFieldInjectorSingleLevelSpecified() {
        FieldInjector fieldInjector = FieldInjectorFactory.getFieldInjector(injector, false);
        assertInstanceOf(MultiLevelFieldInjector.class, fieldInjector);

        fieldInjector = FieldInjectorFactory.getFieldInjector(injector, true);
        assertInstanceOf(SingleLevelFieldInjector.class, fieldInjector);
    }
}
