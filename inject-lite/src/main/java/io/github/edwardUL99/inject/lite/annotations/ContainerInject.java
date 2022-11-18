package io.github.edwardUL99.inject.lite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which if used along with Injectable specifies that this dependency should be injected
 * in containers matching the provided IDs. Containers it can be injected into can also be filtered by providing an array of IDs.
 * get that injectable. By default, not injected into injectors not in container contexts, but that can be configured
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ContainerInject {
    /**
     * The array of container IDs this injectable should be registered for. If empty, all containers get the dependency
     * @return the array of container IDs the injectable should be registered for
     */
    String[] value() default {};

    /**
     * If true, the dependency will only be injected into containers
     * @return true if injected only in container injectors and not other injectors
     */
    boolean containerOnly() default true;
}
