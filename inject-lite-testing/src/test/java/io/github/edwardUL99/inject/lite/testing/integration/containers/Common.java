package io.github.edwardUL99.inject.lite.testing.integration.containers;

import io.github.edwardUL99.inject.lite.annotations.ContainerInject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;

@Injectable("common")
// should be injected in containerOne and other non-container injectors with containerOnly false
@ContainerInject(value = "containerOne", containerOnly = false)
public class Common implements Parent {
}
