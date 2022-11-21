package io.github.edwardUL99.inject.lite.exceptions;

/**
 * Thrown when an object that cannot be instantiated is annotated with Injectable
 */
public class InvalidInjectableException extends InjectionException {
    /**
     * Construct the exception with the provided class
     * @param cls the annotated class
     */
    public InvalidInjectableException(Class<?> cls) {
        super(String.format("%s cannot be registered as an injectable. The class must be:\n" +
                "\t a) public,\n" +
                "\t b) not abstract,\n" +
                "\t c) not an interface and,\n" +
                "\t d) not an annotation", cls));
    }
}
