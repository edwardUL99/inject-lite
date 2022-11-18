package io.github.edwardUL99.inject.lite.sample.project.controllers;

import io.github.edwardUL99.inject.lite.sample.project.models.Order;
import io.github.edwardUL99.inject.lite.sample.project.services.OrderService;
import io.github.edwardUL99.inject.lite.testing.MockDependency;
import io.github.edwardUL99.inject.lite.testing.TestInject;
import io.github.edwardUL99.inject.lite.testing.junit.TestInjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(TestInjectionExtension.class)
public class OrdersControllerTest {
    // replace orderServiceBean with a mock
    @MockDependency("orderServiceBean")
    private OrderService mockOrderService;

    // instantiate the variable, injecting dependencies
    @TestInject
    private OrdersController ordersController;

    @Test
    public void shouldGetOrder() {
        int id = 1;
        Order order = new Order(id, "name", 1, 10.00);

        when(mockOrderService.getOrder(id))
                .thenReturn(Optional.of(order));

        Order returned = ordersController.getOrder(id);

        assertEquals(returned, order);
        verify(mockOrderService).getOrder(id);
    }

    @Test
    public void shouldNotGetOrderIfNotExists() {
        int id = 1;

        when(mockOrderService.getOrder(id))
                .thenReturn(Optional.empty());

        Order returned = ordersController.getOrder(id);

        assertNull(returned);
        verify(mockOrderService).getOrder(id);
    }

    @Test
    public void shouldFindOrderByName() {
        String name = "name";
        Order order = new Order(1, name, 1, 10.00);

        when(mockOrderService.getOrders())
                .thenReturn(Collections.singletonList(order));

        Order returned = ordersController.findOrderByName(name);

        assertEquals(returned, order);
        verify(mockOrderService).getOrders();
    }
}
