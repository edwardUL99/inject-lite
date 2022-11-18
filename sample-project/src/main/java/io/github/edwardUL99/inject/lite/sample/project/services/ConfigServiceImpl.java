package io.github.edwardUL99.inject.lite.sample.project.services;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.sample.project.models.Config;

// available in all containers and normal injectors
@Injectable("configServiceBean")
public class ConfigServiceImpl implements ConfigService {
    private final Config config = new Config();

    @Override
    public Config getConfig() {
        return config;
    }
}
