package io.github.edwardUL99.inject.lite.internal.threads;

/**
 * This thread is used when you want to share injection from one thread with another,
 * since global injectors are thread-scoped.
 * Useful when running asynchronous code and want to use same injector in asynchronous threads
 */
public class SharedInjectionThread extends InjectionAwareThread {
    /**
     * The injection thread the thread is running under
     */
    private final Thread injectionThread;

    /**
     * Create a thread instance
     * @param runnable the thread runnable
     * @param injectionThread the thread owning the injector=
     */
    public SharedInjectionThread(Runnable runnable, Thread injectionThread) {
        super(runnable);
        this.injectionThread = injectionThread;
    }

    /**
     * Get the thread instance owning the injector
     * @return the parent container thread
     */
    public Thread getInjectionThread() {
        return injectionThread;
    }

    @Override
    public final boolean isParent() {
        return false;
    }

    @Override
    public final boolean isChild() {
        return true;
    }
}
