package dev.mustafa.mini_marqetplace.model.dto;

public record RegisterRequest(
        String name,
        String userName,
        String password
) {
}
