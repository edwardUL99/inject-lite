package io.github.edwardUL99.inject.lite.testing;

import io.github.edwardUL99.inject.lite.annotations.processing.AnnotationScanner;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.DefaultAnnotationScanner;
import io.github.edwardUL99.inject.lite.exceptions.InjectionException;
import io.github.edwardUL99.inject.lite.internal.container.ContainersInternal;
import io.github.edwardUL99.inject.lite.internal.injector.DefaultInjectorFactory;
import io.github.edwardUL99.inject.lite.internal.injector.InjectorFactory;
import io.github.edwardUL99.inject.lite.internal.injector.InjectionContext;
import io.github.edwardUL99.inject.lite.internal.annotations.processing.ScannersContext;
import io.github.edwardUL99.inject.lite.internal.threads.Threads;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.mockito.Mockito.mock;

/**
 * This class allows for injection to be started in a test context. The entrypoint to the injection test harness.
 * The test injection context is best used being executed from a single thread and therefore, may not be fully compatible with
 * containers provided in the {@link io.github.edwardUL99.inject.lite.container.Containers} API. It should be considered experimental
 * if this is done.
 * The following is a sample test class with injection in a test context:
 * <pre>{@code
 *     @literal  @ExtendWith(TestInjectionExtension.class)
 *     public class FooTest {
 *         @literal @TestResource("fooDependency")
 *         private FooDependency dependency;
 *
 *         @literal @Resource("service")
 *         private ActualService service;
 *
 *         @literal  @TestInject
 *         private Foo foo;
 *
 *         @literal @Test
 *         public void testFoo() {
 *             when(dependency.service())
 *                  .thenReturn("Hello World");
 *
 *             String returnVal = foo.method(service.config());
 *
 *             assertEquals("From FooDependency: Hello World", returnVal);
 *             verify(dependency).service();
 *         }
 *     }
 * }</pre>
 * <p>In the above code, you can imagine that Foo is a class which uses FooDependency to get a value. Foo simply returns
 * the value with From FooDependency prepended onto it based on some provided config. Foo is a class that either has a {@link io.github.edwardUL99.inject.lite.annotations.Inject}
 * annotation on a field of type FooDependency or a constructor annotated with {@link io.github.edwardUL99.inject.lite.annotations.Inject}
 * where the constructor accepts a value of type FooDependency. (Therefore, it can be injected with TestInject). </p>
 *
 * <p>The TestResource annotation does 3 things:
 * a) Create a mock object of type FooDependency
 * b) Register the mock with the test injector so it can be injected if clients request it
 * c) Set the field with the mock object
 * Mocks are created using Mockito which is provided as a dependency in this harness.</p>
 *
 * <p>If you want a real object you want to inject, define a field and annotate it with the {@link io.github.edwardUL99.inject.lite.annotations.Inject}
 * , note however, that if there is a mocked instance with the same name/type, the mocked instance will be injected. Therefore, you should
 * always use a name in the annotation rather than relying on the type, and ensuring you don't create TestResources with th same name/type.
 * When you want to instantiate the object under test using dependency injection, use the TestInject annotation. It has the same requirements as the
 * {@link io.github.edwardUL99.inject.lite.annotations.Inject} annotation:
 * a) The class must either have a no-arg constructor or a single constructor annotated with the Inject annotation,
 * b) If a no-arg constructor is provided, it can't be annotated with Inject. Instead, inject fields directly
 * using the Resource annotation. Note that depending on mocked fields, some fields of the class will be mocked
 * TestResources are processed first, and then TestInjects are processed after.
 * These variables are not injected until start is called.</p>
 *
 * <p>The {@link io.github.edwardUL99.inject.lite.testing.junit.TestInjectionExtension} is a JUnit 5 extension
 * which automatically starts and ends the context before and after each test method. This is the recommended
 * way to use this test harness, rather than directly calling start and stop in this class</p>
 *
 * <p>You need to have the inject-lite, Mockito and JUnit Jupiter dependencies on your classpath for the harness to work</p>
 */
public final class TestInjection {
    private TestInjection() {}

    // register fields annotated with MockDependency
    private static void registerDependencyFields(TestInjector injector, Object testClass) {
        for (Field field : testClass.getClass().getDeclaredFields()) {
            try {
                if (!Modifier.isFinal(field.getModifiers())) {
                    MockDependency dependency = field.getAnnotation(MockDependency.class);

                    if (dependency != null) {
                        field.setAccessible(true);
                        Object mocked = mock(field.getType());
                        field.set(testClass, mocked);
                        injector.registerTestDependency(dependency.value(), mocked);
                    }
                }
            } catch (ReflectiveOperationException ex) {
                throw new InjectionException("Failed to create MockDependency", ex);
            }
        }
    }

    // set fields annotated with TestInject
    private static void injectTestFields(TestInjector injector, Object testClass) {
        for (Field field : testClass.getClass().getDeclaredFields()) {
            try {
                if (!Modifier.isFinal(field.getModifiers())) {
                    TestInject inject = field.getAnnotation(TestInject.class);

                    if (inject != null) {
                        field.setAccessible(true);
                        field.set(testClass, injector.instantiate(field.getType()));
                    }
                }
            } catch (ReflectiveOperationException ex) {
                throw new InjectionException("Failed to inject TestInject", ex);
            }
        }
    }

    /**
     * Starts the test injection context. Should eventually be followed by a call to end. Any requests to
     * get injectors/inject dependencies made between calls of start and end test injection are done using a test
     * injector.
     * In the provided testClass, this method injects any TestResource annotated fields with mocked instances
     * of that field and registers the mock to the test injector, and injects TestInject annotated fields with injected
     * instances.
     * It's important to call end when finished as any calls for injection that wants to use normal injection
     * will be done in a test context.
     *
     * @param testClass the test class object
     */
    public static void start(Object testClass) {
        InjectorFactory factory = new TestInjectorFactory();
        InjectionContext.setInjectorFactory(factory);
        // TODO test this logic
        ContainersInternal.setContainerInjectEnabled(false);
        TestInjector injector = (TestInjector) Injector.get();

        AnnotationScanner scanner = new DefaultAnnotationScanner(injector);
        ScannersContext.setSingletonScanner(scanner);

        registerDependencyFields(injector, testClass);
        injectTestFields(injector, testClass);
        // inject any @Inject annotated fields
        injector.getFieldInjector().injectFields(testClass);
    }

    /**
     * Ends the test injection context and returns injection to the normal injection implementation
     */
    public static void end() {
        Thread currentThread = Threads.getCurrentThread();
        InjectionContext.destroySingletonInjector(currentThread);
        ScannersContext.destroySingletonScanner(currentThread);
        InjectionContext.setInjectorFactory(new DefaultInjectorFactory());
        ContainersInternal.setContainerInjectEnabled(true);
    }
}
