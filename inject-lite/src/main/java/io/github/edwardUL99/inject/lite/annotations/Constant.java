package io.github.edwardUL99.inject.lite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a constant value to be injected
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Constant {
    /**
     * The name of the dependency
     * @return the name. By default, the constant field name is used
     */
    String value() default "";
}
