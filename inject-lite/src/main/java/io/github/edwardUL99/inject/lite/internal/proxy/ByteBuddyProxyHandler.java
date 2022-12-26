package io.github.edwardUL99.inject.lite.internal.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Empty;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * A handler for managing ByteBuddy interceptions to DynamicProxies
 */
public class ByteBuddyProxyHandler implements ProxyHandler {
    private <T> DynamicType.Builder<T> subclassWithDefaultConstructor(ByteBuddy builder, Class<T> proxiedType) {
        Constructor<?>[] constructors = proxiedType.getDeclaredConstructors();

        if (constructors.length == 0 || Arrays.stream(constructors).anyMatch(c -> c.getParameters().length == 0)) {
            return builder.subclass(proxiedType);
        } else {
            Constructor<?> constructor = constructors[0];

            return builder.subclass(proxiedType,
                    new ConstructorStrategy.ForDefaultConstructor(
                            ElementMatchers.takesArguments(constructor.getParameterTypes())));
        }
    }

    private <T> DynamicType.Builder<T> subclassProxy(Class<T> proxiedType, ProxyInterceptor handler) {
        return subclassWithDefaultConstructor(new ByteBuddy(), proxiedType)
                .implement(ByteBuddyProxy.class)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(handler));
    }

    @Override
    public <T> T setupProxy(Class<T> proxiedType, ProxiedInvocationHandler proxy) throws ReflectiveOperationException {
        try (DynamicType.Unloaded<?> unloaded = subclassProxy(proxiedType, new ByteBuddyInterceptor(proxy)).make()) {
            return (T) unloaded.load(Proxies.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor().newInstance();
        } catch (IOException ex) {
            throw new ReflectiveOperationException(ex);
        }
    }

    /**
     * An interceptor for ByteBuddy
     */
    public static class ByteBuddyInterceptor extends ProxyInterceptor {
        /**
         * Create the interceptor
         * @param proxy the dynamic proxy object
         */
        public ByteBuddyInterceptor(ProxiedInvocationHandler proxy) {
            super(proxy);
        }

        // This byte buddy hook will call the main intercept method
        @RuntimeType
        public Object intercept(@This Object self,
                                @Origin Method method,
                                @AllArguments Object[] args,
                                @SuperMethod(nullIfImpossible = true) Method superMethod,
                                @Empty Object defaultValue) throws Throwable {
            return intercept(self, method, args);
        }
    }
}
