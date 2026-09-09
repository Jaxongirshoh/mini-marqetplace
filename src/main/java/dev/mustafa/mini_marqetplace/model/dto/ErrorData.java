package dev.mustafa.mini_marqetplace.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record ErrorData(String message, Object... args) {
}
