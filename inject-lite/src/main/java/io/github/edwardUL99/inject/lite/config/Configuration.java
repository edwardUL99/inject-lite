package io.github.edwardUL99.inject.lite.config;

import io.github.edwardUL99.inject.lite.injector.Injector;

/**
 * Provides configuration for the inject-lite library
 */
public final class Configuration {
    /**
     * Array of injection package prefixes to restrict injection scoping
     */
    private static String[] injectionPackagePrefixes;
    /**
     * Determines if field injection should only happen on immediate class or parent classes also if in an inheritance
     * hierarchy where parents contain fields to inject
     */
    private static boolean singleLevelInjection;
    /**
     * In the case of multiple matching dependencies, if true, this selects the first dependency matching
     */
    private static boolean selectFirstDependency;
    /**
     * In the case of multiple matching dependencies and no name is provided, rather than using a strategy to select
     * the dependency, an exception should be thrown
     */
    private static boolean requireNamedMultipleMatch;

    /**
     * Get the array of injection package prefixes that restrict injection scoping.
     * @return the array of injection package prefixes
     */
    public static String[] getInjectionPackagePrefixes() {
        return injectionPackagePrefixes;
    }

    /**
     * Set the injection package prefixes. This must be called before the first call to {@link Injector#get()}.
     * Can only be set once as after that call, any changes are not reflected
     * @param injectionPackagePrefixes the array of package prefixes
     */
    public static void setInjectionPackagePrefixes(String...injectionPackagePrefixes) {
        Configuration.injectionPackagePrefixes = injectionPackagePrefixes;
    }

    /**
     * Determines if single level injection is enabled/disabled when injecting Inject annotated fields. If disabled,
     * inherited fields annotated with Inject is injected also, otherwise only fields declared by the dependency class
     * are injected
     * @return true if enabled, false if not
     */
    public static boolean isSingleLevelInjection() {
        return singleLevelInjection;
    }

    /**
     * Set the value of single level injection, enabling/disabling it
     * @param singleLevelInjection true if enabled, false if not
     */
    public static void setSingleLevelInjection(boolean singleLevelInjection) {
        Configuration.singleLevelInjection = singleLevelInjection;
    }

    /**
     * Determines if the first dependency in the list of multiple matching dependencies should be returned
     * @return true if first matching dependency should be returned
     */
    public static boolean isSelectFirstDependency() {
        return selectFirstDependency;
    }

    /**
     * Set the value for select first dependency. If true, and you attempt to use Inject with no named dependencies,
     * the first matching dependency will be selected.
     * @param selectFirstDependency true to select first, otherwise use a priority based selection
     */
    public static void setSelectFirstDependency(boolean selectFirstDependency) {
        Configuration.selectFirstDependency = selectFirstDependency;
    }

    /**
     * If this returns true and unnamed dependencies are being injected, an exception is thrown if multiple matching dependencies
     * are found. This indicates that if there are ambiguous dependencies, an exception is thrown. Otherwise, if false,
     * a strategy will be used to select the dependency
     * @return true if an ambiguous dependency exception should be thrown
     */
    public static boolean isRequireNamedMultipleMatch() {
        return requireNamedMultipleMatch;
    }

    /**
     * Set the value for requiring named dependencies if there are multiple matches. See the getter for the description
     * @param requireNamedMultipleMatch true if required, false if not
     */
    public static void setRequireNamedMultipleMatch(boolean requireNamedMultipleMatch) {
        Configuration.requireNamedMultipleMatch = requireNamedMultipleMatch;
    }
}
