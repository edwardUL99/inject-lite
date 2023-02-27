package io.github.edwardUL99.inject.lite.testing.integration.dependencies.duplicates;

import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.Principal;

@Injectable("child2")
@Principal
public class Child2 implements Parent {
    @Override
    public String getName() {
        return "Child 2";
    }
}
