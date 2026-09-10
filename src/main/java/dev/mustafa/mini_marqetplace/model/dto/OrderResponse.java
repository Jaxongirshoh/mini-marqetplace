package dev.mustafa.mini_marqetplace.model.dto;

import dev.mustafa.mini_marqetplace.model.entity.enums.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Integer id,
        Integer userId,
        OrderStatus status,
        String idempotencyKey,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}
