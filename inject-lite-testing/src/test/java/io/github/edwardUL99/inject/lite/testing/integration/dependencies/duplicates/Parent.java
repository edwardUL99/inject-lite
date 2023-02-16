package io.github.edwardUL99.inject.lite.testing.integration.dependencies.duplicates;

public interface Parent {
    default String getName() {
        return "Parent";
    }
}
