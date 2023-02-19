package io.github.edwardUL99.inject.lite.testing.integration.hooks;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.LazyInvocation;
import io.github.edwardUL99.inject.lite.injector.Injector;

import java.lang.reflect.Method;

@Injectable("dependency2")
public class LazyDependency {
    public static Method lazilyInvoked;
    public static Injector lazilyInvokedInjector;
    public static boolean targetCalled;

    @LazyInvocation
    public void lazilyInvoked(Injector injector, Method method) {
        lazilyInvoked = method;
        lazilyInvokedInjector = injector;
    }

    public void target() {
        targetCalled = true;
    }
}
