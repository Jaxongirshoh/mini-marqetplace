package dev.mustafa.mini_marqetplace.service;

import dev.mustafa.mini_marqetplace.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    private final OrderRepository orderRepository;

    public OrderItemService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
