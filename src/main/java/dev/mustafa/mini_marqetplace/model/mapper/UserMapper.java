package dev.mustafa.mini_marqetplace.model.mapper;

import dev.mustafa.mini_marqetplace.model.entity.User;
import dev.mustafa.mini_marqetplace.model.entity.enums.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    private UserMapper(){
    }

    private static final UserMapper INSTANCE = new UserMapper();


    public static UserMapper getInstance(){
        return INSTANCE;
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("username"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role")),
                rs.getTimestamp("created_at")
        );
    }
}
