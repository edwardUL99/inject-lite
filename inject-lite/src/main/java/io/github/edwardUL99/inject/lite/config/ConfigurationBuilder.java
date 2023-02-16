package io.github.edwardUL99.inject.lite.config;

import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.config.Configuration;

/**
 * A builder for building configuration
 */
public class ConfigurationBuilder {
    /**
     * The configuration object being built
     */
    private final Configuration configuration = new Configuration();

    /**
     * Set the injection package prefixes. This must be called before the first call to {@link Injector#get()}.
     * Can only be set once as after that call, any changes are not reflected
     * @param packagePrefixes the array of package prefixes
     */
    public ConfigurationBuilder withInjectionPackagePrefixes(String...packagePrefixes) {
        configuration.setInjectionPackagePrefixes(packagePrefixes);

        return this;
    }

    /**
     * Set the value of single level injection, enabling/disabling it. If disabled,
     * inherited fields annotated with Inject is injected also, otherwise only fields declared by the dependency class
     * are injected
     * @param singleLevelInjection true if enabled, false if not
     */
    public ConfigurationBuilder withSingleLevelInjection(boolean singleLevelInjection) {
        configuration.setSingleLevelInjection(singleLevelInjection);

        return this;
    }

    /**
     * Set the value for select first dependency. If true, and you attempt to use Inject with no named dependencies,
     * the first matching dependency will be selected.
     * @param selectFirstDependency true to select first, otherwise use a priority based selection
     * @deprecated See deprecation notice on {@link #withRequireNamedMultipleMatch(boolean)}. When that is removed,
     * the strategy to select from multiple dependencies will be to name the dependency or to annotate a single subtype
     * woth {@link io.github.edwardUL99.inject.lite.annotations.Main}
     */
    @Deprecated
    public ConfigurationBuilder withSelectFirstDependency(boolean selectFirstDependency) {
        configuration.setSelectFirstDependency(selectFirstDependency);

        return this;
    }

    /**
     * Set the value for requiring named dependencies if there are multiple matches. See the getter for the description
     * @param requireNamedMultipleMatch true if required, false if not
     * @deprecated in a future release, this will be unconfigurable and a name will need to be provided if a multiple
     * match is found
     */
    @Deprecated
    public ConfigurationBuilder withRequireNamedMultipleMatch(boolean requireNamedMultipleMatch) {
        configuration.setRequireNamedMultipleMatch(requireNamedMultipleMatch);

        return this;
    }

    /**
     * Set the value for using parameter/property names if the dependency is unnamed.
     * @param useParameterNameIfUnnamed true to use the parameter/property name, false to use the type
     */
    public ConfigurationBuilder withUseParameterNameIfUnnamed(boolean useParameterNameIfUnnamed) {
        configuration.setUseParameterNameIfUnnamed(useParameterNameIfUnnamed);

        return this;
    }

    /**
     * Set the value for enabling Lazy annotations. The default is true. If false, performance may be improved,
     * however, you need to be stricter with avoiding circular dependencies
     * @param lazyDependenciesEnabled true to enable lazy dependency proxies, false to disable.
     */
    public ConfigurationBuilder withLazyDependenciesEnabled(boolean lazyDependenciesEnabled) {
        configuration.setLazyDependenciesEnabled(lazyDependenciesEnabled);

        return this;
    }

    /**
     * Build the configuration object
     * @return the built configuration
     */
    public Configuration build() {
        return configuration;
    }
}
