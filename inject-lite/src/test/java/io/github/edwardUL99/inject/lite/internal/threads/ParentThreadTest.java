package io.github.edwardUL99.inject.lite.internal.threads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParentThreadTest {
    private ParentThread parentThread;
    @BeforeEach
    void init() {
        parentThread = new ParentThread(() -> System.out.println("runnable"));
    }

    @Test
    void isParent() {
        assertTrue(parentThread.isParent());
    }

    @Test
    void isChild() {
        assertFalse(parentThread.isChild());
    }
}