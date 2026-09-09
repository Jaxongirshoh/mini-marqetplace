package dev.mustafa.mini_marqetplace.repository;

import dev.mustafa.mini_marqetplace.model.entity.User;
import dev.mustafa.mini_marqetplace.model.entity.enums.Role;
import dev.mustafa.mini_marqetplace.model.mapper.UserMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private static final String FIND_BY_EMAIL_QUERY = "select * from users where email = :email";


    private final JdbcClient jdbcClient;

    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<User> findByEmail(String email) {
        return jdbcClient.sql(FIND_BY_EMAIL_QUERY)
                .param("email", email)
                .query(UserMapper.getInstance())
                .optional();
    }
}
