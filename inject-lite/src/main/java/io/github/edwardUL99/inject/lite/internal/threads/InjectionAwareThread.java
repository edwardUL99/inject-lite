package io.github.edwardUL99.inject.lite.internal.threads;

/**
 * Represents a thread that is aware of being an injection parent or child thread
 */
public abstract class InjectionAwareThread extends Thread {
    /**
     * Create the thread with the target runnable
     * @param target the runnable object
     */
    public InjectionAwareThread(Runnable target) {
        super(target);
    }

    /**
     * Determines if the thread is a parent thread of child threads spawned from this thread
     * @return true if parent or not
     */
    public abstract boolean isParent();

    /**
     * Determines if the thread is a child thread spawned from a parent thread
     * @return true if a child thread
     */
    public abstract boolean isChild();
}
