package dev.mustafa.mini_marqetplace.model.entity;

import dev.mustafa.mini_marqetplace.model.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String idempotencyKey;
    private OrderStatus status;
    private Timestamp createdAt;
}
