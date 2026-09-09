package dev.mustafa.mini_marqetplace.service;

import dev.mustafa.mini_marqetplace.exception.AlreadyExistException;
import dev.mustafa.mini_marqetplace.exception.InvalidRequestException;
import dev.mustafa.mini_marqetplace.exception.NotFoundException;
import dev.mustafa.mini_marqetplace.model.dto.*;
import dev.mustafa.mini_marqetplace.model.entity.RefreshToken;
import dev.mustafa.mini_marqetplace.model.entity.User;
import dev.mustafa.mini_marqetplace.model.entity.enums.Role;
import dev.mustafa.mini_marqetplace.repository.RefreshTokenRepository;
import dev.mustafa.mini_marqetplace.repository.UserRepository;
import dev.mustafa.mini_marqetplace.util.JwtUtil;
import dev.mustafa.mini_marqetplace.util.TokenHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token.ttl}")
    private long ACCESS_TOKEN_TTL_SECONDS;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existByUserName(request.userName())) {
            throw new AlreadyExistException("User with %s username already exist".formatted(request.userName()));
        }
        User user = new User();
        user.setRole(Role.USER);
        user.setName(request.name());
        user.setUsername(request.userName());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new NotFoundException("user with %s username not found".formatted(loginRequest.username())));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        TokenResponse tokenResponse = issueTokens(user);

        return tokenResponse;

    }

    @Transactional
    public void logout(LogoutRequest logoutRequest) {
        refreshTokenRepository.revokedByToken(TokenHasher.hash(logoutRequest.refreshToken()));
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        String rawToken = refreshTokenRequest.refreshToken();

        if (!jwtUtil.isValid(rawToken)) {
            throw new InvalidRequestException("Invalid or expired refresh token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(TokenHasher.hash(rawToken))
                .orElseThrow(() -> new InvalidRequestException("Refresh token not recognized"));

        if (!storedToken.isValid()) {
            throw new InvalidRequestException("Refresh token is no longer valid, please log in again");
        }

        refreshTokenRepository.revokedByToken(storedToken.getToken());

        Integer userId = storedToken.getUser_id();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found with %s id".formatted(userId)));

        return issueTokens(user);
    }

    private TokenResponse issueTokens(User user) {
        Map<String, Object> claims = Map.of("id", user.getId(),
                "role", user.getRole().name());
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), claims);
        return new TokenResponse(
                accessToken,
                refreshToken,
                ACCESS_TOKEN_TTL_SECONDS
        );
    }
}
