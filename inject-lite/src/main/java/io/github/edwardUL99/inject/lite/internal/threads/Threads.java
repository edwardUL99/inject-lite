package io.github.edwardUL99.inject.lite.internal.threads;

import java.util.function.Supplier;

/**
 * Utilities for working with threads
 */
public final class Threads {
    /**
     * Supplies current thread
     */
    private static Supplier<Thread> threadSupplier;

    static {
        setThreadSupplier(Thread::currentThread);
    }

    private Threads() {}

    /**
     * Set the supplier for the current thread
     * @param threadSupplier supplies current thread
     */
    static void setThreadSupplier(Supplier<Thread> threadSupplier) {
        Threads.threadSupplier = threadSupplier;
    }

    /**
     * Get the current thread to use for retrieving thread-aware injectors/scanners. If the thread is a shared injection thread,
     * the injection thread is returned
     * @return the current thread
     */
    public static Thread getCurrentThread() {
        return getInjectionThread(threadSupplier.get());
    }

    /**
     * If this thread has a parent injection thread, return it
     * @param thread the thread to retrieve the parent injection thread for
     * @return parent of the thread, or just thread if no parent injection thread
     */
    public static Thread getInjectionThread(Thread thread) {
        if (thread instanceof SharedInjectionThread) {
            Thread injectionThread = ((SharedInjectionThread)thread).getInjectionThread();

            if (injectionThread == null)
                throw new IllegalStateException("SharedInjectionThread has a null reference to the thread" +
                        " owning the injector. Shared injection threads cannot be dangling");

            return injectionThread;
        }

        return thread;
    }

    /**
     * Determines if the provided thread is an injection parent or child thread
     * @param thread the thread to query
     * @return true if an injection parent or child thread, false if not
     */
    public static boolean isInjectionAwareThread(Thread thread) {
        if (thread instanceof InjectionAwareThread) {
            InjectionAwareThread awareThread = (InjectionAwareThread) thread;

            return awareThread.isChild() || awareThread.isParent();
        }

        return false;
    }
}
