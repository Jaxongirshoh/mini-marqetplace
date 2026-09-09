package dev.mustafa.mini_marqetplace.controller;

import dev.mustafa.mini_marqetplace.model.dto.*;
import dev.mustafa.mini_marqetplace.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<String>> register(
            @RequestBody RegisterRequest request
    ) {
        authService.register(request);
        return ResponseEntity.ok(new BaseResponse<>("Created!"));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<TokenResponse>> login(
            @RequestBody LoginRequest loginRequest
    ) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(new BaseResponse<>(tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logout(
            @RequestBody LogoutRequest logoutRequest
    ) {
        authService.logout(logoutRequest);
        return ResponseEntity.ok(new BaseResponse<>("logged out"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<TokenResponse>> refresh(
            @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        TokenResponse tokenResponse = authService.refresh(refreshTokenRequest);
        return ResponseEntity.ok(new BaseResponse<>(tokenResponse));
    }
}
