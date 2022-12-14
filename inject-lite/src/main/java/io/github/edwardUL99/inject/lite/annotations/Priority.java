package io.github.edwardUL99.inject.lite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation determines priority for dependencies in the case that multiple are available to be injected
 * and one needs to be chosen
 * @deprecated In a future release, any ambiguity will be disallowed, meaning that this annotation will no longer make
 * sense
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
public @interface Priority {
    /**
     * The priority, the default is max integer
     * @return dependency priority
     */
    int value() default Integer.MAX_VALUE;
}
