package io.github.edwardUL99.inject.lite.testing.integration.hooks;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.hooks.ConstructedHook;
import io.github.edwardUL99.inject.lite.hooks.PreConstructHook;
import io.github.edwardUL99.inject.lite.injector.Injector;

@Injectable("dependency")
public class DependencyInterface implements PreConstructHook, ConstructedHook {
    public static Injector preConstructed;
    public Injector constructed;

    public static void preConstruct(Injector injector) {
        preConstructed = injector;
    }

    @Override
    public void constructed(Injector injector) {
        constructed = injector;
    }
}
