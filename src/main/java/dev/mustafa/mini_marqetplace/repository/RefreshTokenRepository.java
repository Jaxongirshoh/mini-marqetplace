package dev.mustafa.mini_marqetplace.repository;

import dev.mustafa.mini_marqetplace.model.entity.RefreshToken;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class RefreshTokenRepository {

    private final JdbcClient jdbcClient;
    private static final String REVOKE_TOKEN_QUERY = "update RefreshToken rt set rt.revoked = true where rt.token = :hashedToken and rt.revoked = false";
    private static final String FIND_BY_TOKEN = "select * from refresh_token rt where rt.token = :token";

    public RefreshTokenRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<RefreshToken> findByToken(String hashedToken) {
        return jdbcClient.sql(FIND_BY_TOKEN)
                .param("token", hashedToken)
                .query((rs, nu) -> new RefreshToken(
                                rs.getInt("id"),
                                rs.getString("token"),
                                rs.getInt("user_id"),
                                rs.getTimestamp("expire_at").toInstant(),
                                rs.getBoolean("revoked")
                        )
                )
                .optional();
    }


    public void revokedByToken(String hashedToken) {
        jdbcClient.sql(REVOKE_TOKEN_QUERY)
                .param("hashedToken", hashedToken)
                .update();
    }
}
