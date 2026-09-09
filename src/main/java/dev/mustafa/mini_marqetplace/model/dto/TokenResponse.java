package dev.mustafa.mini_marqetplace.model.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {
}
