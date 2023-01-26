package io.github.edwardUL99.inject.lite.sample.project.services;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.hooks.LazyInvocation;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.sample.project.models.Config;

import java.lang.reflect.Method;

// available in all containers and normal injectors
@Injectable("configServiceBean")
public class ConfigServiceImpl implements ConfigService, LazyInvocation {
    private final Config config = new Config();

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void lazilyInvoked(Injector injector, Method method) {
        System.out.println("ConfigService Lazy Invocation of method: " + method.getName());
    }

    @Override
    public boolean onlyInvokeFirst() {
        return false;
    }
}
