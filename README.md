inject-lite
==
The inject-lite library provides a lightweight easy-to-use dependency injection library driven by annotations. It allows
for the definition of dependencies and the later injection of those dependencies into clients depending on
them.

It is a simple library, providing the bare minimum required for dependency injection. It has the ability of running
injection in single threaded or multithreaded contexts. Each thread gets access to its own
global injector. An injector is the base object publicly available in the library which has the ability to inject
dependencies.

## Requirements
- To build the library, Maven 3.6.3 is required.
- The minimum Java version supported is Java 8.

## Get the library
Add the following dependency to your project:
```xml
<dependency>
  <groupId>io.github.edwardUL99</groupId>
  <artifactId>inject-lite</artifactId>
  <version>1.0.0</version>
</dependency> 
```

## Annotations
There are three main builtin annotations that provide the means of registering dependencies and specifying
where to inject these dependencies. The annotations are outlined below

### @Injectable
This annotation can be applied to classes to declare the class as one that should be instantiated and injected as a
dependency in requested classes. The default for registering dependencies is to register singleton instances. However,
the annotation has a `singleton` flag which can be set to false to always create a new instance of this dependency whenever
it is requested. The annotation takes a parameter which is the name of the dependency. This name is used to retrieve the dependency
later on in injection. The following is a sample service marked as a dependency
```java
import io.github.edwardUL99.inject.lite.annotations.Injectable;

// The interface of the dependency
public interface ExampleService {
    void doService();
}

// The implementation of the example service. You annotate the implementation with Injectable
@Injectable("exampleServiceImpl")
public class ExampleServiceImpl implements ExampleService {
    @Override
    public void doService() {
        System.out.println("Hello World");
    }
}
```
In the above example, the service is specified with the interface `ExampleService`. The implementation is provided with
the class `ExampleServiceImpl`. You annotate the implementation with `@Injectable("exampleServiceImpl")` which registers
a singleton of the class as a dependency with name `exampleServiceImpl`

The following requirements are imposed on an injectable class:
1. The class must be a public class
2. It must be a concrete type
3. The class must not be abstract or an interface
4. If the class does not have a no-arg constructor, all the arguments in the constructor must have dependencies
registered for them and the constructor must be annotated with the `@Inject` annotation.

### @Inject
This annotation specifies a field that should have its value injected with a dependencies or a constructor that should have
its parameters injected with dependencies. With fields, you can specify the name of the dependency to find a dependency with
that name (in this case the dependency must be assignable to the type of the field), or you can not specify a name to inject
it with the first dependency found that can be assigned to the type of the field.

In the following example, assume that all the dependencies already exist
```java
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.Inject;

@Injectable("serviceImpl")
public class ServiceImpl implements Service {
    // First dependency that can be assigned to ServiceDependency will be injected here
    @Inject
    private ServiceDependency dependency;
    // find a dependency named otherServiceImpl and inject into otherService
    @Inject("otherServiceImpl")
    private OtherService otherService;
    // will be injected in the constructor, as you can see, this allows the field to be final
    private final ConstructorService constructorService;
    
    @Inject
    public ServiceImpl(ConstructorService constructorService) {
        this.constructorService = constructorService;
    }
    
    @Override
    public void service() {
        // use dependencies
    }
}
```

### @Name
The name annotation can be used in a constructor annotated with @Inject to name the dependency. Otherwise, unannotated constructor
parameters are injected by type. The following example illustrates this.
```java
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Name;

@Injectable("serviceImpl")
public class ServiceImpl implements Service {
    // These fields are injected by the constructor
    private final ServiceDependency dependency;
    private final OtherService otherService;
    
    @Inject
    public ServiceImpl(ServiceDependency dependency, @Name("otherServiceImpl") OtherService otherService) {
        // this field is injected by type
        this.dependency = dependency;
        // this field is injected by name
        this.otherService = otherService;
    }
    
    @Override
    public void service() {
        // use dependencies
    }
}
```

### @ContainerInject
This annotation is used alongside `@Injectable`. It specifies that the dependency should be injected in containers with IDs
that match the IDs provided in the annotation. By default, the dependency will only be injected into container injectors
and not non-container injectors. The value property of the annotation defaults to an empty array which indicates that all
containers should receive the dependency.

```java
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.ContainerInject;

// will be injected in all injectors
@Injectable("dependency")
public class Dependency {}

// will be injected in all containers and not non-injector dependencies
@ContainerInject
@Injectable("dependency1")
public class Dependency {}

// will be injected in containers with ID 1 or 2
@ContainerInject({1, 2})
@Injectable("dependency2")
public class Dependency {}

// will be injected in containers with ID 1 or 2, but also non-container injectors
@ContainerInject(value = {1, 2}, containerOnly = false)
@Injectable("dependency3")
public class Dependency {}
```

## Injection
To manually inject dependencies, you can use either `T Injector#inject(String name, Class<T> expected)` or `T Injector#inject(Class<T> type)`.
- The first function injects the dependency by finding a dependency with the given name. It then matches the type of the
dependency against the expected type to ensure that the dependency can be assigned to the type
- The second function returns the first dependency that can be assigned to the provided type

### Injector
You can get an instance of an injector by using `Injector.get()` which retrieves the global injector for the
current thread context. You can also use `Injection#newInjector()` to create a new injector instance that is independent
to the global instance.

Both methods scan for classes annotated with `@Injectable` on the classpath, registers the dependencies to it and returns
the injector. The global injector only does this on the first call. To reset the global injector, call
`Injection.resetGlobalInjector()` which removes the global injector for the current thread (injectors
are on a per-thread context). A subsequent call to `Injector.get()` on the same thread will recreate the injector.

The following code shows how you can use the injector to get an instance of the above ServiceImpl.
```java
import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.injector.Injector;

public class Example {
    public static void main(String[] args) {
        Injector injector = Injector.get();
        // Since ServiceImpl can be assigned to Service, this code works
        Service service = injector.inject("serviceImpl", Service.class);
        // This service will be the same instance as above since 1 dependency ServiceImpl can be assigned to Service
        Service service1 = injector.inject(Service.class);
        
        // Inject ALL dependencies that are either instances of Service or sub-types. inject(Service.class) returns the first matching dependency
        // this returns all matching ones
        List<Service> services = injector.injectAll(Service.class);
        
        service.service();
        service1.service();
        
        for (Service service2 : services) {
            service2.service();
        }
    }
}
```

An injector also has the method `T Injector#instantiate(Class<T> type)` which uses injection to instantiate
the class with dependencies without registering the class as a dependency. This way, you can create a non-singleton instance.
```java
import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.injector.Injector;

public class Example {
    public static void main(String[] args) {
        Injector injector = Injector.get();
        // Since ServiceImpl can be assigned to Service, this code works
        Service service = injector.inject("serviceImpl", Service.class);
        // This service will be the same instance as above since 1 dependency ServiceImpl can be assigned to Service
        Service service1 = injector.inject(Service.class);
        
        // this will be different to the services above even though same type. Note the need to specify the concrete type here
        Service service2 = injector.instantiate(ServiceImpl.class);
        
        service.service();
        service1.service();
        service2.service();
    }
}
```
The Service interface and ServiceImpl class here is defined further up in the README

### Injection with multiple dependencies
When using `Injector#inject(Class)` the injector looks for a dependency where the type of the dependency is either the same
as the provided class or a subclass. The behaviour when trying to inject a dependency of this type and where multiple dependencies
match the type is dependent on configuration. The configuration method of interest are:
- `Configuration.setSelectFirstDependency(boolean)` if true, the first dependency in the list of matching dependencies is returned.
If false, dependencies are selected using priority. Priority can be assigned to a dependency using the `@Priority` annotation.
This annotation takes an integer, where the lower the integer gives the dependency the highest priority. The default is the maximum
integer value. The default is false, meaning priority will be used.

To get all the matching dependencies, use `Injector#injectAll(Class)` which returns a map of the dependencies matching,
keyed by the name of the dependency, with the value being the dependency instance.

#### Unnamed dependencies
In this context, an unnamed dependency is when you annotate either:
- a field with @Inject and without a name, or
- a constructor parameter without @Name annotation

When this happens, the injector is searched for a matching dependency. Where multiple dependencies match based on the type
of the field/parameter, the behaviour again is dependent on configuration.
- If `Configuration.setRequireNamedMultipleMatch(boolean)` is called with true, this means that when injecting an
unnamed dependency and multiple matches are found, an `AmbiguousDependencyException` will be thrown. The default is false.
When false, when multiple dependencies are found, the strategy of injecting based on priority/first in the list will be 
used, again based on `Configuration.setSelectFirstDependency(boolean)`

### Restricting injection scope
By default, every class on the classpath of the project will be searched for dependencies. You can restrict the scope of
where dependencies are searched to a list of base packages where all classes and packages underneath those base packages
will be scanned, rather than the whole project. For example your project package is `com.foo.bar` with multiple child classes
and packages containing dependencies. The project depends on 10 other dependencies. Rather than scanning the project and all
its dependencies, the following call restricts scanning to the project:
```
// Configuration.setInjectionPackagePrefixes(String...packages);

// all classes and subpackages underneath com.foo.bar will be scanned for dependencies
Configuration.setInjectionPackagePrefixes("com.foo.bar");
```
Make this call before you begin using any injectors or annotation scanners, as any calls after will have no effect

Note that this also restricts annotation scanning using the `AnnotationScanner` API.

## Custom Injectable Annotation
While `@Injectable` is the annotation to use for registering dependencies, you can also register your own annotations to
use for registering dependencies using the `CustomInjectableProcessor` processor. This can be passed to the 
`AnnotationScanner` API which scans for custom annotations and registers the annotated classes as dependencies. The following
is an example usage.
```java
import io.github.edwardUL99.inject.lite.annotations.processing.CustomInjectableProcessor;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotaionScanner;
import io.github.edwardUL99.inject.lite.annotations.processing.AnnotaionScanners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CustomDependency {
    String value() default "";
}

@CustomDependency("name")
public static class CustomDependencyInject {
    public void sayHello() {
        System.out.println("Hello World");
    }
}

public class Main {
    public static void dependencyHandler(Object dependency) {
        // should print true
        System.out.println(dependency instanceof CustomDependencyInject);
        ((CustomDependencyInject) dependency).sayHello();
    }
    
    public static void main(String[] args) {
        CustomInjectableProcessor<CustomDependency> processor = new CustomInjectableProcessor<>(
                customAnnotation -> customAnnotation.getAnnotation().value(),
                Main::dependencyHandler
        );
        
        // retrieve a scanner instance
        AnnotationScanner scanner = AnnotationScanners.globalScanner();
        // register the processor instance for the dependency
        scanner.registerAnnotationProcessor(CustomDependency.class, processor);
        // scan for classes annotated with the dependency and pass them to the processor
        scanner.scan(CustomDependency.class);
    }
}
```

## Containers
The `Container` API provides a means of running multiple injection contexts asynchronously. Each container is represented
as a unit of execution, where each execution has their own instance of global Injector's and AnnotationScanners (see custom
injectable annotation). This allows for the following:
- Thread-safe dependency injection. When running injection in multi-threading scenarios, any chances of race conditions
are best avoided. Containers allow this to happen
- Isolated injection environments allow for different dependencies to be injected in each container. Dependencies can be configured
to be only injected inside container environments, which has a further configuration ability to only inject the dependency in
containers with a specified ID. Different means to register different dependencies such as the `registerDependency` method or `CustomInjectableProcessor`
can also be used to register dependencies on a container level configuration. These 2 means can be varied in
each container. Dependencies registered by this means in one container is not seen in any other container.

**Note:** Containers must be executed within a ContainerContext

The `Containers` class has the following methods:
- executeContainer: Using the provided `ContainerBuilder` as a template, it creates and starts a container,
setting up any annotation scanners passed into the builder (if manual scan is set on the builder, annotation scanning will
have to be manually done inside in the container)
- executeSingleContainer: Calls executeContainer and then awaits its completion. While it may not seem like a lot of sense
using containers when you only need one, it allows for features such as:
    - Multithreading inside the container where threads spawned inside the container can access the same injector as the container,
    rather than separate ones
- awaitContainerFinish: Waits for all containers to finish and then returns
- getCurrentContainer: Gets the current container in the context of where the method was called. If no container matches the
thread from where the method was called, null is returned
- containerSafeExecutor: Returns an executor for executing asynchronous code that shares the same injectors as the
container provided to the function
- context: Returns a context to execute containers inside. Used inside a try with resources, it awaits all containers once the try block
finishes executing

```java
import io.github.edwardUL99.inject.lite.container.Container;
import io.github.edwardUL99.inject.lite.container.Containers;
import io.github.edwardUL99.inject.lite.container.ContainerContext;
import io.github.edwardUL99.inject.lite.injector.Injector;

public class Main {
  public static void main(String[] args) {
    try (ContainerContext ignored = Containers.context()) {
      Containers.executeContainer(Container.builder()
          .withId("1")
          .withExecutionUnit(container -> {
            Injector injector = container.getInjector(); // Injector.get() returns the same injector in this container's context
            System.out.println(injector);
         }));

        // injectors will be different in each containers
    
        Containers.executeContainer(Container.builder()
          .withId("2")
          .withExecutionUnit(container -> {
            Injector injector = container.getInjector();
            System.out.println(injector);
         }));

        // this container has no ID so will only get ContainerInject dependencies if no IDs are specified in the annotation
        Containers.executeContainer(Container.builder()
          .withExecutionUnit(container -> {
            Injector injector = container.getInjector();
            System.out.println(injector);
        }));
    
        // When we reach here, the context waits for all containers to finish
     }
  }
}
```

## Multithreading
Multithreading is supported by the library through the `AsynchronousExecutor` API. This executor API provides abstractions
required for providing injection and container-safe async code. It is an abstraction around Java's `ExecutorService` API,
which if used, is not considered to be injection and container safe.

Injection safe means that the global injector used in the main thread is shared with all child threads spawned off the main
thread. This is used for when the `Containers` API is not being used.

Container safe means that any threads spawned inside a container will share the same global injector as the parent container.
This is incredibly important, otherwise, child threads will have different injection results than the parent container,
for example, dependencies registered with '@ContainerInject' will have different dependencies registered in the child threads.
Also, if the container registered dependencies in any other means, these dependencies won't be available to the chilr threads.

For shared injection across multiple threads in a non-Container environment, use the `Injection#sharedInjectionExecutor()` method
which returns an executor that runs child threads which receives the same injector returned by `Injection#globalInjector()`
as the calling thread.

For container environments that use the same injector as the parent container in child threads, use `Container#asyncExecutor()`
which returns an executor that runs async code using same global injector as the container.

**Important:** Any async code executed in any other way than these discussed methods, is not supported and results are undefined.

```java
import io.github.edwardUL99.inject.lite.container.Container;
import io.github.edwardUL99.inject.lite.container.Containers;
import io.github.edwardUL99.inject.lite.container.ContainerContext;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.threads.AsynchronousExecutor;
import io.github.edwardUL99.inject.lite.threads.Execution;

public class Main {
  public static void main(String[] args) {
    try (ContainerContext ignored = Containers.context()) {
      Containers.executeSingleContainer(Container.builder()
          .withId("1")
          .withExecutionUnit(container -> {
            Injector injector = Injector.get();
            AsynchronousExecutor executor = container.asyncExecutor();
          
            Execution execution = executor.schedule(() -> {
                // this will be the same injector as above injector
               Injector injector1 = Injector.get();
            });
          
            execution.awaitFinish();
            executor.shutdown();
          }));
    }
    
    // non-container injector
    Injector injector = Injector.get();
    
    AsynchronousExecutor executor = Injection.sharedInjectorExecutor();
    executor.scheduleAwait(() -> {
      // this will be the same injector as above injector
      Injector injector1 = Injector.get();
    });
    
    executor.shutdown();
  }
}
```

## Inject Lite Testing
The project, provides another dependency called `inject-lite-testing` which provides a testing harness for testing code
which uses the `inject-lite` library. It provides the following features:
- Creation of dependencies mocked using `mockito`
- Injection of test fields

### Dependency
To import the test harness add the following dependencies to your project (the main dependency needs to be added too)
```xml
<dependency>
  <groupId>io.github.edwardUL99</groupId>
  <artifactId>inject-lite-testing</artifactId>
  <version>1.0-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>5.9.0</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>4.8.0</version>
  <scope>test</scope>
</dependency>
```

Junit and Mockito needs to be provided on the classpath, therefore they need to be defined as requirements

### How to use
To use the test harness, you use annotations in the JUnit test class. The following is an example:
```java
import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.testing.MockDependency;
import io.github.edwardUL99.inject.lite.testing.TestInject;
import io.github.edwardUL99.inject.lite.testing.junit.TestInjectionExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Injectable("service")
public class Service {
    // This will be injected in the constructor
    private final ExternalDependency dependency;
    
    @Inject
    public Service(ExternalDependency dependency) {
        this.dependency = dependency;
    }
    
    public String service() {
        return dependency.doExternal();
    }
}

// This extension ensures that the test injection context is spun up and down before and after each test case respectively
// while also processing the field annotations
@ExtendWith(TestInjectionExtension.class)
public class ServiceTest {
    // this field will be set with a Mockito mock and registered as a dependency on the test injector
    @MockDependency("dependency")
    private ExternalDependency mockDependency;
    
    // the service under test. Will be instantiated and injected with dependencies (dependency in the constructor of
    // Service will be the mock created with MockDependency. The instantiated object will be set on this field
    @TestInject
    private Service service;
    
    @Test
    public void testService() {
        // Mock dependency is a Mockito mock, so it can be stubbed
        when(mockDependency.doExternal())
              .thenReturn("Hello World");
        
        String returnVal = service.service();
        
        assertEquals("Hello World", returnVal);
        verify(mockDependency).doExternal();
    }
}
```

## Sample Project
To see a sample project using the library, see the [sample-project](sample-project) directory which has sample services
and "controller" as well as sample tests

## Building the library
To build the library, you can clone the repository and build it with Maven. The requirements for the build are as follows:
- JDK 8 installation (JAVA_HOME should be pointing to this and mvn should use this version)
- JDK 11 installation for JavaDoc.

1. Set the JAVA_HOME path to the home of JDK 8 installation
2. Run `export PATH="$JAVA_HOME/bin:$PATH`
3. Run `export JAVA_DOC_EXEC="$JDK_11_HOME/bin/javadoc` where JDK_11_HOME is the path to the home of your JDK 11 installation.
We use JavaDoc features not available in JDK 8, hence this requirement
4. From the root of the project, run `mvn clean install`
   5. If the build fails due to missing parent POM dependency, in [pom.xml](pom.xml), run `mvn -N clean install` followed by a normal install
