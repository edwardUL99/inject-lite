package io.github.edwardUL99.inject.lite.testing.integration.dependencies;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;

@Injectable("serviceImpl")
public class ServiceImpl implements Service {
    public final StringGetter getter;
    public final Config config;

    @Inject
    public ServiceImpl(StringGetter getter, Config config) {
        this.getter = getter;
        this.config = config;
    }

    @Override
    public String doService() {
        return getter.getString() + ": Version: " + config.getVersion();
    }
}
