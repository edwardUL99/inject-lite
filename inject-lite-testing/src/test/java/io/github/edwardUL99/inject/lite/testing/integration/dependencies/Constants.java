package io.github.edwardUL99.inject.lite.testing.integration.dependencies;

import io.github.edwardUL99.inject.lite.annotations.Constant;
import io.github.edwardUL99.inject.lite.annotations.ConstantDependencies;

@ConstantDependencies
public class Constants {
    @Constant
    public static final Long TEST_VAL = 45L;
}
