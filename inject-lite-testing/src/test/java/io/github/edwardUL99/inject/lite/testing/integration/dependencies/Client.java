package io.github.edwardUL99.inject.lite.testing.integration.dependencies;

import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;

@Injectable("client")
public class Client {
    @Inject
    public Service service;

    public String useService() {
        return service.doService();
    }
}
