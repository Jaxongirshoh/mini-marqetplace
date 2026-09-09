package dev.mustafa.mini_marqetplace.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record BaseResponse<T>(T data, ErrorData error, boolean success) {

    public BaseResponse(T data) {
        this(data, null, true);
    }

    public BaseResponse(ErrorData error) {
        this(null, error, false);

    }
}
