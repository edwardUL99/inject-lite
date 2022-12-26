package io.github.edwardUL99.inject.lite.internal.utils;

import io.github.edwardUL99.inject.lite.internal.threads.Threads;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * This class encapsulates a map of the current Thread to a value of type T. The class allows the retrieval
 * of the value mapped by the current thread. These threads are either container shared threads if specified, otherwise,
 * just the current thread
 */
public class ThreadAwareValue<T> {
    /**
     * The mapping of thread to value
     */
    private final Map<Thread, T> map;
    /**
     * The default value
     */
    private final T defaultValue;
    /**
     * Indicates if the threads are shared with containers
     */
    private final Supplier<Thread> threadSupplier;

    /**
     * Create the value
     * @param defaultValue the default value if thread doesn't have a mapping
     * @param containerSharedThread indicates if the threads are shared with containers
     */
    public ThreadAwareValue(T defaultValue, boolean containerSharedThread) {
        this.map = new ConcurrentHashMap<>();
        this.defaultValue = defaultValue;
        threadSupplier = (containerSharedThread) ? Threads::getCurrentThread : Thread::currentThread;
    }

    /**
     * Get the thread value or default
     * @return value
     */
    public synchronized T getValue() {
        return map.getOrDefault(threadSupplier.get(), defaultValue);
    }

    /**
     * Set the thread value
     * @param value the new value
     */
    public synchronized void setValue(T value) {
        map.put(threadSupplier.get(), value);
    }

    /**
     * Similar to map computeIfAbsent but uses current thread as key. If a value for the thread exists, it returns the value,
     * otherwise, it inserts the value returned by the supplier and then returns that value
     * @param value the value supplier
     * @return the new or existing value
     */
    public synchronized T getValueOrInsert(Supplier<T> value) {
        return map.computeIfAbsent(threadSupplier.get(), k -> value.get());
    }

    /**
     * Get a copy of the underlying map
     * @return the map
     */
    public synchronized Map<Thread, T> getAllValuesAsMap() {
        return map;
    }

    /**
     * Remove the mapping for the provided thread
     * @param thread the thread
     */
    public synchronized void remove(Thread thread) {
        map.remove(thread);
    }
}
