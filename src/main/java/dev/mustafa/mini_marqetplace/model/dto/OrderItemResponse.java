package dev.mustafa.mini_marqetplace.model.dto;

public record OrderItemResponse(
        Integer id,
        Integer productId,
        Integer quantity,
        Double price
) {
}
