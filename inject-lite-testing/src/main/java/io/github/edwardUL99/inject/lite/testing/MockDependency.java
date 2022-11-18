package io.github.edwardUL99.inject.lite.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a field as a resource for use in testing. The test injector does the following with fields annotated with this:
 * a) Creates a mock of the field and sets the field with the mock object
 * b) Registers the mock to the test injector so that the mock is injected to clients requesting it
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MockDependency {
    /**
     * The name of the dependency
     * @return the name
     */
    String value();
}
