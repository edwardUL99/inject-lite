package io.github.edwardUL99.inject.lite.internal.container;

import io.github.edwardUL99.inject.lite.internal.threads.ParentThread;
import io.github.edwardUL99.inject.lite.internal.threads.SharedInjectionThread;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

public class ContainerThreadFactoryTest {
    @Test
    public void testFactoryWithParentContainerThread() {
        Runnable runnable = mock(Runnable.class);
        ContainerThreadFactory factory = new ContainerThreadFactory(null);

        Thread returned = factory.newThread(runnable);

        assertInstanceOf(ParentThread.class, returned);
    }

    @Test
    public void testFactoryWithContainerThread() {
        Runnable runnable = mock(Runnable.class);

        Thread thread = new ParentThread(runnable);
        thread.setName("Parent");
        ContainerThreadFactory factory = new ContainerThreadFactory(thread);

        Thread returned = factory.newThread(runnable);

        assertInstanceOf(SharedInjectionThread.class, returned);
    }
}
