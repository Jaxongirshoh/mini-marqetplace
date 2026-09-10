package dev.mustafa.mini_marqetplace.model.dto;

import java.util.List;

public record OrderCreateDto(
        String idempotencyKey,
        List<OrderItemCreateDto> items
) {
}
