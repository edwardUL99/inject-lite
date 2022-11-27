package io.github.edwardUL99.inject.lite.testing.integration.dependencies;

import io.github.edwardUL99.inject.lite.annotations.Injectable;

@Injectable("goodbyeGetter")
public class GoodbyeWorldGetter implements StringGetter {
    @Override
    public String getString() {
        return "Goodbye World";
    }
}
