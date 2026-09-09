package dev.mustafa.mini_marqetplace.model.entity;

import dev.mustafa.mini_marqetplace.model.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private Role role;
    private Timestamp createdAt;
}
