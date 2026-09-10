package dev.mustafa.mini_marqetplace.repository;

import dev.mustafa.mini_marqetplace.model.dto.OrderResponse;
import dev.mustafa.mini_marqetplace.model.entity.Order;
import dev.mustafa.mini_marqetplace.model.entity.enums.OrderStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {
    private final JdbcClient jdbcClient;
    private static final String FIND_BY_IDEMPOTENCY_KEY_QUERY = "select id, user_id, status, idempotency_key, created_at from orders where idempotency_key = :key";
    private static final String UPDATE_STOCK_QUERY = "update products set stock_quantity = :stock where id = :id";
    private static final String ORDER_CREATE_QUERY = "insert into orders (user_id, status, idempotency_key) values (:userId, :status, :key) returning id";
    private static final String FIND_BY_ID_QUERY = "select * from orders where id = :id";
    private static final String UPDATE_ORDER_STATUS_QUERY = "update orders set status = :status where id = :id";
    private static final String FIND_EXPIRED_ORDERS = "select id from orders where status = 'PENDING' and created_at < NOW() - (INTERVAL '1 minute' * :minutes) for update skip locked ";

    public OrderRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<OrderResponse> findByIdempotencyKey(String key) {
        return jdbcClient.sql(FIND_BY_IDEMPOTENCY_KEY_QUERY)
                .param("key", key)
                .query((rs, nu) -> new OrderResponse(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        OrderStatus.valueOf(rs.getString("status")),
                        rs.getString("idempotency_key"),
                        rs.getTimestamp("created_at").toInstant(),
                        List.of()
                ))
                .optional();
    }


    public void updateStock(Integer id, int newStock) {
        jdbcClient.sql(UPDATE_STOCK_QUERY)
                .param("stock",newStock)
                .param("id",id)
                .update();
    }

    public Integer createOrder(Integer userId, OrderStatus status, String key) {
        return jdbcClient.sql(ORDER_CREATE_QUERY)
                .param("userId", userId)
                .param("status", status)
                .param("key", key)
                .query(Integer.class)
                .single();
    }

    public Optional<Order> findById(Integer orderId) {
        return jdbcClient.sql(FIND_BY_ID_QUERY)
                .param("id", orderId)
                .query((rs, nu) -> new Order(
                                rs.getInt("id"),
                                rs.getInt("user_id"),
                                rs.getInt("product_id"),
                                rs.getString("idempotency_key"),
                                OrderStatus.valueOf(rs.getString("status")),
                                rs.getTimestamp("created_at")
                        )
                )
                .optional();
    }

    public void updateOrderStatus(Integer id, OrderStatus orderStatus) {
        jdbcClient.sql(UPDATE_ORDER_STATUS_QUERY)
                .param("status", orderStatus)
                .param("id", id)
                .update();
    }

    public List<Integer> findExpiredPendingOrders(int timeInMinutes) {
        return jdbcClient.sql(FIND_EXPIRED_ORDERS)
                .param("minutes", timeInMinutes)
                .query(Integer.class)
                .list();
    }
}
