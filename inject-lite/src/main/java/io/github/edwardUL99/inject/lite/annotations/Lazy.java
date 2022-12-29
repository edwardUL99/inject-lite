package io.github.edwardUL99.inject.lite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a dependency that is lazily injected. A lazily injected object is a proxy which is instantiated on the
 * first use of that object.
 * Dependencies that are the target of a Lazy annotation should not be final classes as they need to be extended for
 * proxy creation
 */
@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Lazy {
}
