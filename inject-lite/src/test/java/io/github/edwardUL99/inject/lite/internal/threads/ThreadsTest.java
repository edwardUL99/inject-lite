package io.github.edwardUL99.inject.lite.internal.threads;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static io.github.edwardUL99.inject.lite.utils.TestUtils.setInternalStaticField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.getInternalState;

public class ThreadsTest {
    private static Supplier<Thread> original;
    private Supplier<Thread> mockThreadSupplier;

    @BeforeAll
    public static void staticInit() {
        original = getInternalState(Threads.class, "threadSupplier");
    }

    @AfterAll
    public static void staticTeardown() {
        setInternalStaticField(Threads.class, "threadSupplier", original);
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void init() {
        mockThreadSupplier = (Supplier<Thread>) mock(Supplier.class);
        setInternalStaticField(Threads.class, "threadSupplier", mockThreadSupplier);
    }

    @Test
    public void testCurrentThreadNormal() {
        Thread returnVal = new Thread(mock(Runnable.class));
        when(mockThreadSupplier.get())
                .thenReturn(returnVal);

        Thread currentThread = Threads.getCurrentThread();

        assertEquals(returnVal, currentThread);
        verify(mockThreadSupplier).get();
    }

    @Test
    public void testCurrentThreadShared() {
        Thread parent = new ParentThread(mock(Runnable.class));
        Thread returnVal = new SharedInjectionThread(mock(Runnable.class), parent);
        when(mockThreadSupplier.get())
                .thenReturn(returnVal);

        Thread currentThread = Threads.getCurrentThread();

        assertEquals(parent, currentThread);
        verify(mockThreadSupplier).get();
    }

    @Test
    public void testCurrentThreadSharedNoParent() {
        Thread returnVal = new SharedInjectionThread(mock(Runnable.class), null);
        when(mockThreadSupplier.get())
                .thenReturn(returnVal);

        assertThrows(IllegalStateException.class, Threads::getCurrentThread);
        verify(mockThreadSupplier).get();
    }
}
