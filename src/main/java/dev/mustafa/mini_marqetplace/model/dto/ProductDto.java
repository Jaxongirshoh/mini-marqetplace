package dev.mustafa.mini_marqetplace.model.dto;

public record ProductDto(
        String name,
        double price,
        int stockQuantity
) {
}
