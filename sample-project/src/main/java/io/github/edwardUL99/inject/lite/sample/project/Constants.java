package io.github.edwardUL99.inject.lite.sample.project;

import io.github.edwardUL99.inject.lite.annotations.Constant;
import io.github.edwardUL99.inject.lite.annotations.ConstantDependencies;

@ConstantDependencies
public class Constants {
    @Constant("debug")
    public static final boolean debug = true;
}
