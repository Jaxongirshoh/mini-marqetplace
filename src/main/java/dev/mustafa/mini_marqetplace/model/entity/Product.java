package dev.mustafa.mini_marqetplace.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {
    private Integer id;
    private String name;
    private Double price;
    private Integer stockQuantity;
    private Timestamp createdAt;
}
