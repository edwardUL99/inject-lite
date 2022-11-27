package io.github.edwardUL99.inject.lite.testing.integration.dependencies;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.Priority;

@Priority(1)
@Injectable("helloGetter")
public class HelloWorldGetter implements StringGetter {
    @Override
    public String getString() {
        return "Hello World";
    }
}
