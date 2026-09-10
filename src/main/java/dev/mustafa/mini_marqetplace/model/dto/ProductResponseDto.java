package dev.mustafa.mini_marqetplace.model.dto;

public record ProductResponseDto(
        Integer id,
        String name,
        double price,
        int stock_quantity
) {
}
