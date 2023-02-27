package io.github.edwardUL99.inject.lite.sample.project;

import io.github.edwardUL99.inject.lite.Injection;
import io.github.edwardUL99.inject.lite.config.ConfigurationBuilder;
import io.github.edwardUL99.inject.lite.container.Container;
import io.github.edwardUL99.inject.lite.container.ContainerContext;
import io.github.edwardUL99.inject.lite.container.Containers;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.sample.project.controllers.AccountController;
import io.github.edwardUL99.inject.lite.sample.project.controllers.OrdersController;
import io.github.edwardUL99.inject.lite.sample.project.duplicates.Client;
import io.github.edwardUL99.inject.lite.sample.project.models.Account;
import io.github.edwardUL99.inject.lite.sample.project.models.Order;

public class Main {
    private static void getOrderAndPrint(int id, OrdersController controller) {
        Order order = controller.getOrder(id);

        if (order != null) {
            System.out.printf("Found order: %s\n", order);
        } else {
            System.out.printf("Order with ID %d not found\n", id);
        }
    }

    private static void getOrderAndPrint(String name, OrdersController controller) {
        Order order = controller.findOrderByName(name);

        if (order != null) {
            System.out.printf("Found order: %s\n", order);
        } else {
            System.out.printf("Order with name %s not found\n", name);
        }
    }

    private static void executeOrdersContainer(Container ordersContainer) {
        System.out.println();

        // simulate order requests coming in
        Injector injector = ordersContainer.getInjector();
        OrdersController ordersController = injector.inject("ordersControllerBean", OrdersController.class);
        getOrderAndPrint(1, ordersController);
        getOrderAndPrint(2, ordersController);
        getOrderAndPrint("order", ordersController);
        getOrderAndPrint("order1", ordersController);

        System.out.println("Debug property in orders: " + injector.inject("debug", boolean.class));

        System.out.println();
    }

    private static void getAccountAndPrint(int id, AccountController controller) {
        Account account = controller.findAccountById(id);

        if (account != null) {
            System.out.printf("Found account: %s\n", account);
        } else {
            System.out.printf("Account with ID %d not found\n", id);
        }
    }

    private static void authenticateAndPrint(String username, String password, AccountController controller) {
        boolean authenticated = controller.authenticate(username, password);

        if (authenticated) {
            System.out.printf("User %s authenticated\n", username);
        } else {
            System.out.printf("User %s not authenticated\n", username);
        }
    }

    private static void executeAccountsContainer(Container accountsContainer) {
        System.out.println();

        // simulate account requests
        Injector injector = accountsContainer.getInjector();
        AccountController accountController = injector.inject("accountControllerBean", AccountController.class);

        getAccountAndPrint(1, accountController);
        getAccountAndPrint(2, accountController);

        try {
            Thread.sleep(5000);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        authenticateAndPrint("username", "password", accountController);
        authenticateAndPrint("username", "password1", accountController);
        authenticateAndPrint("username1", "password", accountController);

        System.out.println("Debug property in accounts: " + injector.inject("debug", boolean.class));

        System.out.println();
    }

    private static void samplePrincipalDependency() {
        Client client = Injector.get().inject(Client.class);
        System.out.println("child1 name should be Child 1 = " + client.getChild1().getName());
        System.out.println("child2 name should be Child 2 = " + client.getChild2().getName());
        System.out.println("unknown child's name should be Child 2 since child2 dependency is " +
                "annotated with Principal = " + client.getUnknown().getName());
    }

    public static void main(String[] args) {
        Injection.configure(
                new ConfigurationBuilder()
                        .withRequireNamedMultipleMatch(true)
                        .withInjectionPackagePrefixes("io.github.edwardUL99.inject.lite.sample.project")
        );

        samplePrincipalDependency();

        try (ContainerContext ignored = Containers.context()) {
            Container container = Containers.executeContainer(
                    Container.builder()
                            .withId("ordersContainer")
                            .withExecutionUnit(Main::executeOrdersContainer)
            );

            container.await();

            container = Containers.executeContainer(
                    Container.builder()
                            .withId("accountsContainer")
                            .withExecutionUnit(Main::executeAccountsContainer)
            );

            container.await();
        }
    }
}
