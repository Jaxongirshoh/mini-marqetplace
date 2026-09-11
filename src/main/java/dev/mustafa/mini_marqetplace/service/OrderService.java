package dev.mustafa.mini_marqetplace.service;

import dev.mustafa.mini_marqetplace.exception.InvalidRequestException;
import dev.mustafa.mini_marqetplace.exception.NotFoundException;
import dev.mustafa.mini_marqetplace.exception.OutOfStockException;
import dev.mustafa.mini_marqetplace.model.dto.OrderCreateDto;
import dev.mustafa.mini_marqetplace.model.dto.OrderItemResponse;
import dev.mustafa.mini_marqetplace.model.dto.OrderResponse;
import dev.mustafa.mini_marqetplace.model.entity.Order;
import dev.mustafa.mini_marqetplace.model.entity.OrderItem;
import dev.mustafa.mini_marqetplace.model.entity.Product;
import dev.mustafa.mini_marqetplace.model.entity.User;
import dev.mustafa.mini_marqetplace.model.entity.enums.OrderStatus;
import dev.mustafa.mini_marqetplace.repository.OrderItemRepository;
import dev.mustafa.mini_marqetplace.repository.OrderRepository;
import dev.mustafa.mini_marqetplace.repository.ProductRepository;
import dev.mustafa.mini_marqetplace.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public OrderResponse createOrder(Integer userId, OrderCreateDto orderCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with %s id not found".formatted(userId)));

        Optional<OrderResponse> byIdempotencyKey = orderRepository.findByIdempotencyKey(orderCreateDto.idempotencyKey());
        if (byIdempotencyKey.isPresent()) {
            return byIdempotencyKey.get();
        }

        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (var item : orderCreateDto.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new NotFoundException("product with %s id not found: ".formatted(item.productId())));

            if (product.getStockQuantity() < item.quantity()) {
                throw new OutOfStockException("insufficient stock for %s product ".formatted(item.productId()));
            }

            int newStock = product.getStockQuantity() - item.quantity();
            orderRepository.updateStock(product.getId(), newStock);

            itemResponses.add(new OrderItemResponse(
                    null,
                    product.getId(),
                    item.quantity(),
                    product.getPrice()
            ));

        }

        Integer orderId = orderRepository.createOrder(userId, OrderStatus.PENDING, orderCreateDto.idempotencyKey());

        for (OrderItemResponse it : itemResponses) {
            orderItemRepository.createOrderItem(orderId, it.productId(), it.quantity(), it.price());
        }

        return new OrderResponse(
                orderId,
                userId,
                OrderStatus.PENDING,
                orderCreateDto.idempotencyKey(),
                Instant.now(),
                itemResponses
        );
    }

    @Transactional
    public void confirmOrder(Integer orderId, Integer userId) {
        Order order = getOrderOrThrow(orderId, userId);

        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new InvalidRequestException("only PENDING orders can be updated current status: " + order.getStatus());
        }

        orderRepository.updateOrderStatus(order.getId(), OrderStatus.CONFIRMED);
    }

    @Transactional
    public OrderResponse getById(Integer orderId, Integer userId) {
        Order order = getOrderOrThrow(orderId, userId);

        List<OrderItemResponse> orderItems = orderItemRepository.getOrderItems(order.getId()).stream()
                .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getProductId(),
                                item.getQuantity(),
                                item.getPrice()
                        )
                ).toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getIdempotencyKey(),
                order.getCreatedAt().toInstant(),
                orderItems
                );
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void cancelOrder(Integer orderId, Integer userId) {
        Order order = getOrderOrThrow(orderId, userId);

        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new InvalidRequestException("only PENDING orders can be updated current status: " + order.getStatus());
        }

        List<OrderItem> items = orderItemRepository.getOrderItems(order.getId());
        for (OrderItem item : items) {
            productRepository.restoreProductStock(item.getProductId(), item.getQuantity());
        }

        orderRepository.updateOrderStatus(order.getId(), OrderStatus.CANCELLED);
    }

    private Order getOrderOrThrow(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("order with %s id not found ".formatted(orderId)));

        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException("access denied");
        }

        return order;
    }
}
