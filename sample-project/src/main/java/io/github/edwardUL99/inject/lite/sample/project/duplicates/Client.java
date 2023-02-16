package io.github.edwardUL99.inject.lite.sample.project.duplicates;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.Name;

@Injectable("client")
public class Client {
    @Inject
    @Name("child1")
    private Parent child1;

    @Inject
    @Name("child2")
    private Parent child2;

    @Inject
    private Parent unknown;

    public Parent getChild1() {
        return child1;
    }

    public Parent getChild2() {
        return child2;
    }

    public Parent getUnknown() {
        return unknown;
    }
}
