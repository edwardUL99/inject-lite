package io.github.edwardUL99.inject.lite.testing.integration.dependencies;

import io.github.edwardUL99.inject.lite.annotations.Injectable;

@Injectable("")
public class Config {
    public String getVersion() {
        return "1.2.0";
    }
}
