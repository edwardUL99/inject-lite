package io.github.edwardUL99.inject.lite.sample.project.services;

import io.github.edwardUL99.inject.lite.annotations.ContainerInject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.sample.project.models.Order;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// will be only available in container with ID orderContainer
@ContainerInject("ordersContainer")
@Injectable("orderServiceBean")
public class OrderServiceImpl implements OrderService {
    private final Order fakeOrder;

    public OrderServiceImpl() {
        fakeOrder = new Order(1, "order", 1, 10.00);
    }

    @Override
    public Optional<Order> getOrder(int id) {
        if (id == fakeOrder.getId()) {
            return Optional.of(fakeOrder);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Order> getOrders() {
        return Collections.singletonList(fakeOrder);
    }
}
