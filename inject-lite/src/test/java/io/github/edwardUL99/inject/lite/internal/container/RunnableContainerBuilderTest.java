package io.github.edwardUL99.inject.lite.internal.container;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationProcessor;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationScanner;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationScanners;
import io.github.edwardUL99.inject.lite.container.ContainerAnnotationProcessor;
import io.github.edwardUL99.inject.lite.container.ExecutionUnit;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RunnableContainerBuilderTest {
    private ExecutionUnit mockUnit;
    private ContainerAnnotationProcessor<?> mockProcessor;
    private List<ContainerAnnotationProcessor<?>> processors;
    private AsynchronousExecutor mockExecutor;

    @BeforeEach
    public void init() {
        mockUnit = mock(ExecutionUnit.class);
        mockProcessor = mock(ContainerAnnotationProcessor.class);
        processors = Collections.singletonList(mockProcessor);
        mockExecutor = mock(AsynchronousExecutor.class);
        Injection.resetGlobalInjector();
    }

    @Test
    public void testWithExecutionUnit() {
        RunnableContainerBuilder builder = new RunnableContainerBuilder();
        assertNull(builder.id);
        assertNull(builder.executionUnit);

        builder.withExecutionUnit(mockUnit);

        assertEquals(mockUnit, builder.executionUnit);
    }

    @Test
    public void testWithAnnotationProcessors() {
        RunnableContainerBuilder builder = new RunnableContainerBuilder();
        assertNull(builder.id);
        assertEquals(new ArrayList<>(), builder.processors);
        assertFalse(builder.annotationProcessorsUsed);
        assertNull(builder.executionUnit);

        builder.withAnnotationProcessors(processors);

        assertEquals(processors, builder.processors);
        assertTrue(builder.annotationProcessorsUsed);
    }

    @Test
    public void testWithManualAnnotationScan() {
        RunnableContainerBuilder builder = new RunnableContainerBuilder();
        assertNull(builder.id);
        assertFalse(builder.manualScan);

        builder.withManualAnnotationScan(true);

        assertTrue(builder.manualScan);
    }

    @Test
    public void testWithId() {
        RunnableContainerBuilder builder = new RunnableContainerBuilder();
        assertNull(builder.id);
        assertNull(builder.executionUnit);

        builder.withId("id");

        assertEquals("id", builder.id);
    }

    @Test
    public void testWithExecutor() {
        RunnableContainerBuilder builder = new RunnableContainerBuilder();
        assertNull(builder.id);
        assertNull(builder.executor);

        builder.withExecutor(mockExecutor);

        assertEquals(mockExecutor, builder.executor);
    }

    @Test
    public void testBuildNoProcessors() {
        RunnableContainerBuilder builder = (RunnableContainerBuilder) new RunnableContainerBuilder()
                .withId("id")
                .withExecutionUnit(mockUnit);

        RunnableContainer container = builder.build();

        assertNotNull(container);
        assertEquals("id", container.getId());
        assertNotNull(container.getExecutionUnit());
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testBuildWithProcessors() {
        try (MockedStatic<AnnotationScanners> scanners = mockStatic(AnnotationScanners.class);
             MockedStatic<Injector> injector = mockStatic(Injector.class)) {
            RunnableContainerBuilder builder = (RunnableContainerBuilder) new RunnableContainerBuilder()
                    .withId("id")
                    .withExecutionUnit(mockUnit)
                    .withAnnotationProcessors(processors);

            AnnotationScanner mockScanner = mock(AnnotationScanner.class);
            scanners.when(AnnotationScanners::globalScanner)
                    .thenReturn(mockScanner);
            injector.when(Injector::get)
                    .thenReturn(mock(Injector.class));
            AnnotationProcessor<?> processor = mock(AnnotationProcessor.class);
            when(mockProcessor.getProcessor())
                    .thenReturn((AnnotationProcessor) processor);
            when(mockProcessor.getAnnotation()).thenReturn(null);

            RunnableContainer container = builder.build();

            assertNotNull(container);
            assertEquals("id", container.getId());
            assertNotEquals(mockUnit, container.getExecutionUnit());

            container.getExecutionUnit().execute(container);

            verify(mockScanner).registerAnnotationProcessor(eq(null), (AnnotationProcessor)eq(processor));
            verify(mockScanner).scan(null);
            reset(mockScanner);

            builder = (RunnableContainerBuilder) new RunnableContainerBuilder()
                    .withId("id")
                    .withExecutionUnit(mockUnit)
                    .withAnnotationProcessors(processors)
                    .withManualAnnotationScan(true);

            container = builder.build();

            container.getExecutionUnit().execute(container);

            verify(mockScanner).registerAnnotationProcessor(eq(null), (AnnotationProcessor)eq(processor));
            verify(mockScanner, times(0)).scan(null);
            reset(mockScanner);
        }
    }
}
