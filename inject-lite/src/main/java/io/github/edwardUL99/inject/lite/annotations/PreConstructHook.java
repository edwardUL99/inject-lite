package io.github.edwardUL99.inject.lite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation equivalent of {@link io.github.edwardUL99.inject.lite.hooks.PreConstruct}, but multiple
 * static methods can be annotated. If multiple are annotated, the order of execution is not defined.
 * Annotated methods are executed before the interface constructed method, so you can use
 * annotations without implementing the interfaces
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreConstructHook {
}
