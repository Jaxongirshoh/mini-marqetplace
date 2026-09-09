package dev.mustafa.mini_marqetplace.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefreshToken {
    private Integer id;
    private String token;
    private Integer user_id;
    private Instant expireAt;
    private boolean revoked = false;

    public boolean isValid() {
        return !revoked && Instant.now().isBefore(expireAt);
    }
}
