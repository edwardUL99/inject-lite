package io.github.edwardUL99.inject.lite.sample.project.services;

import io.github.edwardUL99.inject.lite.sample.project.models.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Optional<Order> getOrder(int id);

    List<Order> getOrders();
}
