package io.github.edwardUL99.inject.lite.internal.threads;

/**
 * This thread is used when you want to share injection from one thread with another,
 * since global injectors are thread-scoped.
 * Useful when running asynchronous code and want to use same injector in asynchronous threads
 */
public class SharedInjectionThread extends Thread {
    /**
     * The injection thread the thread is running under
     */
    private final Thread injectionThread;
    /**
     * Indicates if the thread is a container thread or not
     */
    private final boolean containerThread;

    /**
     * Create a thread instance
     * @param runnable the thread runnable
     * @param injectionThread the thread owning the injector
     */
    public SharedInjectionThread(Runnable runnable, Thread injectionThread) {
        this(runnable, injectionThread, false);
    }

    /**
     * Create a thread instance
     * @param runnable the thread runnable
     * @param injectionThread the thread owning the injector
     * @param containerThread true if a container thread, false if not
     */
    public SharedInjectionThread(Runnable runnable, Thread injectionThread, boolean containerThread) {
        super(runnable);
        this.injectionThread = injectionThread;
        this.containerThread = containerThread;
    }

    /**
     * Get the thread instance owning the injector
     * @return the parent container thread
     */
    public Thread getInjectionThread() {
        return injectionThread;
    }

    /**
     * Determine if the thread is a container thread or not
     * @return true if a container thread, false if not
     */
    public boolean isContainerThread() {
        return containerThread;
    }
}
