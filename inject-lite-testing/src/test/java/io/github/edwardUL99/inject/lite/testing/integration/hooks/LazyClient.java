package io.github.edwardUL99.inject.lite.testing.integration.hooks;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.Lazy;

@Injectable("lazyClient")
public class LazyClient {
    @Lazy
    @Inject
    public LazyDependency lazyDependency;
}
