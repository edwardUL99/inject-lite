package io.github.edwardUL99.inject.lite.annotations.processing;

import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.InternalAnnotatedClass;
import io.github.edwardUL99.inject.lite.internal.container.ContainersInternal;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategies;
import io.github.edwardUL99.inject.lite.internal.dependency.registration.RegistrationStrategy;
import io.github.edwardUL99.inject.lite.internal.dependency.InjectableDependency;
import io.github.edwardUL99.inject.lite.internal.injector.InternalInjector;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This processor allows you to treat the annotation as a custom Injectable, injecting the annotated class
 * to the injector. Built in injection simply just registers the dependency. When defining custom injectables,
 * you can add custom processing logic on the instantiated dependency using the instantiatedProcessor consumer.
 * Without the consumer, it simply registers the dependency for later instantiation without passing it to the consumer
 * @param <T> the type of the annotation
 */
public class CustomInjectableProcessor<T extends Annotation> implements AnnotationProcessor<T> {
    /**
     * The injector to add the dependencies to
     */
    private InternalInjector injector;
    /**
     * The supplier of the name from the provided function
     */
    private final NameSupplier<T> nameSupplier;
    /**
     * A consumer to process the instantiated dependency
     */
    private final Consumer<Object> instantiatedProcessor;

    /**
     * Construct the processor with the provided injector and name supplier
     * @param injector the injector to add the annotated class to
     * @param nameSupplier the supplier of the injectable name
     * @param instantiatedProcessor a consumer to process the instantiated object
     */
    public CustomInjectableProcessor(Injector injector, NameSupplier<T> nameSupplier, Consumer<Object> instantiatedProcessor) {
        this.injector = (InternalInjector) injector;
        this.nameSupplier = nameSupplier;
        this.instantiatedProcessor = instantiatedProcessor;
    }

    /**
     * Construct the processor with the global injector and name supplier
     * @param nameSupplier the supplier of the injectable name
     * @param instantiatedProcessor a consumer to process the instantiated object
     */
    public CustomInjectableProcessor(NameSupplier<T> nameSupplier, Consumer<Object> instantiatedProcessor) {
        this(null, nameSupplier, instantiatedProcessor);
    }

    /**
     * Construct the processor with the provided injector and name supplier
     * @param injector the injector to add the annotated class to
     * @param nameSupplier the supplier of the injectable name
     */
    public CustomInjectableProcessor(Injector injector, NameSupplier<T> nameSupplier) {
        this(injector, nameSupplier, null);
    }

    /**
     * Construct the processor with the global injector and name supplier
     * @param nameSupplier the supplier of the injectable name
     */
    public CustomInjectableProcessor(NameSupplier<T> nameSupplier) {
        this(Injector.get(), nameSupplier, null);
    }

    private void register(Injector injector, InternalAnnotatedClass<T> internal, String name) {
        Class<?> annotatedType = internal.getAnnotatedClass();
        InjectableDependency dependency = internal.getInjectableDependency();
        internal.setInjectableDependency(dependency.withDifferentName(name));
        RegistrationStrategy registrationStrategy = RegistrationStrategies.forCustom(internal);
        boolean injected = ContainersInternal.registerDependencyCheckingContainer(injector, registrationStrategy,
                annotatedType);

        if (injected) {
            Object object = injector.inject(name, annotatedType);

            if (instantiatedProcessor != null)
                instantiatedProcessor.accept(object);
        }
    }

    @Override
    public void process(List<AnnotatedClass<T>> classes) {
        InternalInjector injector = (this.injector == null) ?
                (InternalInjector) Injector.get() : this.injector;

        for (AnnotatedClass<T> annotatedClass : classes) {
            InternalAnnotatedClass<T> internal = (InternalAnnotatedClass<T>) annotatedClass;
            CustomAnnotation<T> custom = new CustomAnnotation<>(annotatedClass.getAnnotation(), annotatedClass.getAnnotatedClass());
            String name = nameSupplier.apply(custom);

            if (name == null) {
                throw new InjectionException("Cannot register a dependency with a null name");
            } else {
                register(injector, internal, name);
            }
        }
    }

    /**
     * Takes the annotation and produces the name based on the annotation
     * @param <A> the type of the annotation
     */
    public interface NameSupplier<A extends Annotation> extends Function<CustomAnnotation<A>, String> { }

    /**
     * Represents a custom injectable annotation being registered
     * @param <A> the type of the annotation
     */
    public static class CustomAnnotation<A extends Annotation> {
        /**
         * The annotation being registered
         */
        private final A annotation;
        /**
         * The class that's annotated
         */
        private final Class<?> cls;

        /**
         * Register the custom annotation
         * @param annotation the custom annotation
         * @param cls the type of the injectable
         */
        public CustomAnnotation(A annotation, Class<?> cls) {
            this.annotation = annotation;
            this.cls = cls;
        }

        /**
         * Get the annotation instance
         * @return annotation instance
         */
        public A getAnnotation() {
            return annotation;
        }

        /**
         * Get the type of the annotated class
         * @return the annotated class
         */
        public Class<?> getType() {
            return cls;
        }
    }
}
