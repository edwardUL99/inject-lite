package io.github.edwardUL99.inject.lite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a constructor/field/method to be injected
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
public @interface Inject {
    /**
     * This value is not used when annotated on a constructor/method, but is used on a field
     * @return the name of the dependency
     * @deprecated use {@link Name} instead to use a named dependency. For now until this value is removed, if a Name
     * annotation is provided, that takes priority, otherwise the value provided here
     */
    @Deprecated
    String value() default "";
}
