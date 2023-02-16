package io.github.edwardUL99.inject.lite.sample.project.duplicates;

public interface Parent {
    default String getName() {
        return "Parent";
    }
}
