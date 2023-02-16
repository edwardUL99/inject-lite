package io.github.edwardUL99.inject.lite.sample.project.duplicates;

import io.github.edwardUL99.inject.lite.annotations.Injectable;

@Injectable("child1")
public class Child1 implements Parent {
    @Override
    public String getName() {
        return "Child 1";
    }
}
