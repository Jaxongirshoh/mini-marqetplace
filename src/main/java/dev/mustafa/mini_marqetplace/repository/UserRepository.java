package dev.mustafa.mini_marqetplace.repository;

import dev.mustafa.mini_marqetplace.model.entity.User;
import dev.mustafa.mini_marqetplace.model.mapper.UserMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Repository
public class UserRepository {

    private static final String FIND_BY_USERNAME_QUERY = "select * from users where username = :username";
    private static final String SAVE_USER_QUERY = "insert into users(name,username,password,role,created_at) " +
            "values(:name,:username,:password,:role,:created_at)";
    private static final String FIND_BY_ID_QUERY = "select * from users where id = :id";


    private final JdbcClient jdbcClient;

    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<User> findByUsername(String email) {
        return jdbcClient.sql(FIND_BY_USERNAME_QUERY)
                .param("username", email)
                .query(UserMapper.getInstance())
                .optional();
    }

    public Optional<User> findById(Integer id){
        return jdbcClient.sql(FIND_BY_ID_QUERY)
                .param("id", id)
                .query(UserMapper.getInstance())
                .optional();
    }


    public void save(User user) {
        jdbcClient.sql(SAVE_USER_QUERY)
                .param("name", user.getName())
                .param("username", user.getUsername())
                .param("password", user.getPassword())
                .param("role", user.getRole())
                .param("createdAt", Timestamp.from(Instant.now()))
                .update();
    }

    public boolean existByUserName(String username) {
        return jdbcClient.sql(FIND_BY_USERNAME_QUERY)
                .param("username", username)
                .query(UserMapper.getInstance())
                .optional().isPresent();
    }


}
