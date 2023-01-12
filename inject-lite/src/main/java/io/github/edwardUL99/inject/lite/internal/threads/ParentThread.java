package io.github.edwardUL99.inject.lite.internal.threads;

/**
 * Represents a parent thread to threads executed inside it
 */
public class ParentThread extends InjectionAwareThread {
    /**
     * Create the parent thread object
     * @param target the target runnable
     */
    public ParentThread(Runnable target) {
        super(target);
    }

    @Override
    public final boolean isParent() {
        return true;
    }

    @Override
    public final boolean isChild() {
        return false;
    }
}
