package io.github.edwardUL99.inject.lite.testing.integration.hooks;

import io.github.edwardUL99.inject.lite.annotations.Constructed;
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.PreConstruct;
import io.github.edwardUL99.inject.lite.injector.Injector;

@Injectable("dependency1")
public class DependencyAnnotations {
    public static Injector preConstructed;
    public static Injector preConstructed1;
    public Injector constructed;
    public Injector constructed1;

    @PreConstruct
    public static void preConstruct(Injector injector) {
        preConstructed = injector;
        preConstructed1 = injector;
    }

    @PreConstruct
    public static void preConstruct1(Injector injector) {
        preConstructed1 = injector;
    }

    @Constructed
    public void postConstruct(Injector injector) {
        constructed = injector;
    }

    @Constructed
    public void postConstruct1(Injector injector) {
        constructed1 = injector;
    }
}
