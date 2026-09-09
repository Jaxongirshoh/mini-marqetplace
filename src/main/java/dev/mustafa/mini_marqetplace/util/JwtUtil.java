package dev.mustafa.mini_marqetplace.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public final class JwtUtil {

    private final SecretKey signingKey;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;
    private final String issuer;
    private static final Logger LOG = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(@Value("${jwt.secret.key}") String secretKey,
                   @Value("${jwt.access-token.ttl}") long accessTokenValidityInSeconds,
                   @Value("${jwt.refresh-token.ttl}") long refreshTokenValidityInSeconds,
                   @Value("${jwt.issuer}") String issuer) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
        this.issuer = issuer;
    }

    public String generateAccessToken(String email, Map<String, Object> claims) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .issuer(issuer)
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
                .claim("token", "ACCESS")
                .claims(claims)
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(String email, Map<String, Object> claims) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .issuer(issuer)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
                .claim("token", "REFRESH")
                .claims(claims)
                .signWith(signingKey)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            LOG.error("Token is not valid: {}", e.getMessage());
        }
        return false;
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "ACCESS".equals(claims.get("token", String.class));
        } catch (Exception e) {
            LOG.error("Token type could not be verified: {}", e.getMessage());
        }
        return false;
    }

}