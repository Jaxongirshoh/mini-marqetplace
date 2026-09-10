package dev.mustafa.mini_marqetplace.repository;

import dev.mustafa.mini_marqetplace.model.entity.OrderItem;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemRepository {
    private final JdbcClient jdbcClient;
    private static final String ORDER_ITEM_CREATE_QUERY = "insert into order_item (order_id, product_id, quantity, price) values (:orderId, :productId, :quantity, :price)";
    private static final String GET_ALL_ORDER_ITEMS_QUERY = "select * from order_item where order_id = :orderId";

    public OrderItemRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void createOrderItem(Integer orderId, Integer productId, Integer quantity, Double price) {
        jdbcClient.sql(ORDER_ITEM_CREATE_QUERY)
                .param("orderId", orderId)
                .param("productId", productId)
                .param("quantity", quantity)
                .param("price", price)
                .update();
    }

    public List<OrderItem> getOrderItems(Integer orderId) {
        return jdbcClient.sql(GET_ALL_ORDER_ITEMS_QUERY)
                .param("orderId", orderId)
                .query((rs, nu) -> new OrderItem(
                                rs.getInt("id"),
                                rs.getInt("order_id"),
                                rs.getInt("product_id"),
                                rs.getInt("quantity"),
                                rs.getDouble("price")
                        )
                )
                .list();
    }
}
