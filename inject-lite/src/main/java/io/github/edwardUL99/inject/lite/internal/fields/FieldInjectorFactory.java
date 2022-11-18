package io.github.edwardUL99.inject.lite.internal.fields;

import io.github.edwardUL99.inject.lite.injector.Injector;

/**
 * A factory for producing field injectors
 */
public final class FieldInjectorFactory {
    /**
     * Determines if single level injection is enabled
     */
    private static final boolean singleLevelInjection = System.getProperty("single.level.injection") != null;

    /**
     * Get a field injector that can inject fields on multiple levels of inheritance
     * @param injector the injector to use for dependencies
     * @return the instance
     */
    public static FieldInjector multiLevelInjector(Injector injector) {
        return new MultiLevelFieldInjector(injector);
    }

    private FieldInjectorFactory() {}

    /**
     * Get a field injector that can inject fields on the single immediate level of inheritance
     * @param injector the injector to use for dependencies
     * @return the instance
     */
    public static FieldInjector singleLevelInjector(Injector injector) {
        return new SingleLevelFieldInjector(injector);
    }

    /**
     * Get a field injector based on a system property being defined
     * @param injector the injector to use for dependencies
     * @return the field injector to use
     */
    public static FieldInjector getFieldInjector(Injector injector) {
        return getFieldInjector(injector, singleLevelInjection);
    }

    /**
     * Get a field injector that is single/multi level
     * @param injector the injector to use for dependencies
     * @param singleLevel true for single level, false for multi level
     * @return the field injector to use
     */
    public static FieldInjector getFieldInjector(Injector injector, boolean singleLevel) {
        return (singleLevel) ? singleLevelInjector(injector):multiLevelInjector(injector);
    }
}
