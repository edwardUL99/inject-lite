package io.github.edwardUL99.inject.lite.testing.integration.containers;

import io.github.edwardUL99.inject.lite.annotations.ContainerInject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;

@Injectable("containerOneDependency")
@ContainerInject("containerOne")
public class ContainerOneDependency implements Parent {
}
