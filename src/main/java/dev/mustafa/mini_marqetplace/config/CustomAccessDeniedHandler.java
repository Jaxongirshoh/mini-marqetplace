package dev.mustafa.mini_marqetplace.config;

import dev.mustafa.mini_marqetplace.model.dto.BaseResponse;
import dev.mustafa.mini_marqetplace.model.dto.ErrorData;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    private static final Logger LOG = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(@NonNull HttpServletRequest request, HttpServletResponse response,
                       @NonNull AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LOG.error("access denied",accessDeniedException);
        response.setHeader("Content-Type","application/json");
        response.setStatus(403);
        ServletOutputStream outputStream = response.getOutputStream();
        ErrorData errorData = new ErrorData("access denied");
        BaseResponse<ErrorData> baseResponse = new BaseResponse<>(errorData);
        objectMapper.writeValue(outputStream,baseResponse);
    }
}
