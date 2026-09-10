package dev.mustafa.mini_marqetplace.model.dto;

public record OrderItemCreateDto(
        Integer productId,
        Integer quantity
) {
}
