package io.github.edwardUL99.inject.lite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that in the case of multiple dependencies of the same type (or subtype) are found, the one
 * annotated with Principal are returned. Note if multiple are found with Principal, an error is thrown
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Principal {
}
