package dev.mustafa.mini_marqetplace.config.security;

import dev.mustafa.mini_marqetplace.model.dto.UserSessionData;
import dev.mustafa.mini_marqetplace.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> EXCLUDE_URLS = List.of(
            "/api/auth/**"
    );

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return EXCLUDE_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()){
            filterChain.doFilter(request,response);
            return;
        }
        if (!authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);
        if (!jwtUtil.isValid(token)){
            filterChain.doFilter(request,response);
            return;
        }
        Claims claims = jwtUtil.getClaims(token);
        if (claims.get("token") == null || "REFRESH".equals(claims.get("token"))) {
            filterChain.doFilter(request,response);
            return;
        }
        String email = jwtUtil.getEmail(token);
        CustomUserDetails userDetails =(CustomUserDetails) userDetailsService.loadUserByUsername(email);
        UserSessionData userSessionData = new UserSessionData(
                userDetails.getId(),
                userDetails.getUsername()
        );

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userSessionData,null,userDetails.getAuthorities());
        WebAuthenticationDetails webAuthDetails =
                new WebAuthenticationDetails(request);
        authToken.setDetails(webAuthDetails);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        LOG.info("Authenticated request for userId={} path={}", userDetails.getId(), request.getServletPath());

        filterChain.doFilter(request,response);
    }
}
