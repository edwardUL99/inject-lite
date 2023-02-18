package io.github.edwardUL99.inject.lite.internal.config;

import io.github.edwardUL99.inject.lite.exceptions.InjectionException;

/**
 * Internal class for providing configuration utilities
 */
public class InternalConfig {
    /**
     * Indicates if the environment is already configured
     */
    private static boolean configured;
    /**
     * Determines if the configured check should be done
     */
    private static boolean disableConfiguredCheck;
    /**
     * The stack trace where the injection was configured
     */
    private static StackTraceElement configuredStackTrace;

    /**
     * Configure the environment using the provided configuration
     * @param configuration the configuration instance
     */
    public static void configure(Configuration configuration) {
        if (!disableConfiguredCheck || !configured) {
            Configuration.global = configuration;
            configured = true;
            configuredStackTrace = Thread.currentThread().getStackTrace()[2];
        } else {
            throw new InjectionException("Injection has already been configured, you cannot configure it more than once.\n" +
                    "It was previously called at: " + configuredStackTrace);
        }
    }

    /**
     * Enables the disabling/enabling of the already configured check
     * @param disableConfiguredCheck value for disableConfiguredCheck
     */
    public static void setDisableConfiguredCheck(boolean disableConfiguredCheck) {
        InternalConfig.disableConfiguredCheck = disableConfiguredCheck;
    }
}
