package io.github.edwardUL99.inject.lite.annotations.processing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used by the annotation scanners to determine the order at which the scanned classes are processed
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProcessOrder {
    /**
     * The priority, the default is max integer
     * @return dependency priority
     */
    int value() default Integer.MAX_VALUE;
}
