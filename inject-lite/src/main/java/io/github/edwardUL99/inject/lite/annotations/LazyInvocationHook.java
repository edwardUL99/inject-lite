package io.github.edwardUL99.inject.lite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation equivalent of {@link io.github.edwardUL99.inject.lite.hooks.LazyInvocation}, but multiple
 * methods can be annotated. If multiple are annotated, the order of execution is not defined. Methods must take a
 * parameter of type Injector and method. Annotated methods are executed before the interface constructed method, so you can use
 * annotations without implementing the interfaces
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LazyInvocationHook {
    /**
     * Equivalent to the {@link io.github.edwardUL99.inject.lite.hooks.LazyInvocation#onlyInvokeFirst()} method
     * @return true to only invoke on first call of method, false if not
     */
    boolean onlyInvokeFirst() default true;
}
