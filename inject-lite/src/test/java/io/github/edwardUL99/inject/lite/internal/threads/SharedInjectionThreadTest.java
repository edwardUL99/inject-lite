package io.github.edwardUL99.inject.lite.internal.threads;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class SharedInjectionThreadTest {
    @Test
    public void testThreadCreation() {
        Runnable mockRunnable = mock(Runnable.class);
        ParentThread mockParent = mock(ParentThread.class);
        SharedInjectionThread sharedInjectionThread = new SharedInjectionThread(mockRunnable, mockParent);

        assertFalse(sharedInjectionThread.isParent());
        assertTrue(sharedInjectionThread.isChild());
        assertEquals(sharedInjectionThread.getInjectionThread(), mockParent);
    }

    @Test
    public void testThreadCreationNonParent() {
        Runnable mockRunnable = mock(Runnable.class);

        assertThrows(IllegalArgumentException.class, () -> new SharedInjectionThread(mockRunnable, new Thread(mockRunnable)));
    }
}