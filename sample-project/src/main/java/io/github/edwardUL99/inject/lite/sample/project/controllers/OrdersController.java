package io.github.edwardUL99.inject.lite.sample.project.controllers;

import io.github.edwardUL99.inject.lite.annotations.ContainerInject;
import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.Name;
import io.github.edwardUL99.inject.lite.sample.project.models.Order;
import io.github.edwardUL99.inject.lite.sample.project.services.ConfigService;
import io.github.edwardUL99.inject.lite.sample.project.services.OrderService;

@ContainerInject("ordersContainer")
@Injectable("ordersControllerBean")
public class OrdersController {
    private final OrderService orderService;

    @Inject
    public OrdersController(OrderService orderService, @Name("configServiceBean") ConfigService configService) {
        this.orderService = orderService;
        System.out.println("Injected ConfigService: " + configService + ". Debug enabled: " + configService.getConfig());
    }

    public Order getOrder(int id) {
        return orderService.getOrder(id)
                .orElse(null);
    }

    public Order findOrderByName(String name) {
        return orderService.getOrders()
                .stream()
                .filter(o -> o.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
