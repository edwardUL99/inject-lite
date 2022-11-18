package io.github.edwardUL99.inject.lite.testing.junit;

import io.github.edwardUL99.inject.lite.testing.TestInjection;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;

/**
 * This extension can be used with JUnit @ExtendsWith to start and end test injection before and after each test
 * method. You need JUnit Jupiter on your classpath for this class. This is the <b>recommended</b> way to use the test
 * harness rather than directly calling {@link TestInjection#start(Object)} or {@link TestInjection#end()} as this
 * extension ensures that the context is implicitly and explicitly started before and after each test
 */
public class TestInjectionExtension implements InvocationInterceptor {
    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        try {
            TestInjection.start(extensionContext.getRequiredTestInstance());
            invocation.proceed();
        } finally {
            TestInjection.end();
        }
    }
}
