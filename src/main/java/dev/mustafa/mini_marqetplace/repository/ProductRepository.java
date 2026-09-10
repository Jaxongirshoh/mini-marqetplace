package dev.mustafa.mini_marqetplace.repository;

import dev.mustafa.mini_marqetplace.model.entity.Product;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcClient jdbcClient;
    private static final String PRODUCT_CREATE_QUERY = "insert into products(name,price,stock_quantity,created_at) values(:name,:price,:stock_quantity,:created_at)";
    private static final String FIND_BY_ID_QUERY = "select * from products where id = :id";
    private static final String UPDATE_QUERY = "update products set name = :name,price = :price, stock_quantity = :stock_quantity where id =:id";
    private static final String PRODUCT_PAGABLE_QUERY = "select id, name, price, stock_quantity, created_at from products order by id desc limit :limit offset :offset";
    private static final String RESTORE_PRODUCT_STOCK_QUERY = "update products set stock_quantity = stock_quantity + :quantity  where id = :id";

    public ProductRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(Product product) {
        jdbcClient.sql(PRODUCT_CREATE_QUERY)
                .param("name", product.getName())
                .param("price", product.getPrice())
                .param("stock_quantity", product.getStockQuantity())
                .param("created_at", Timestamp.from(Instant.now()))
                .update();
    }

    public Optional<Product> findById(Integer id) {
        return jdbcClient.sql(FIND_BY_ID_QUERY)
                .param("id", id)
                .query((rs, nu) -> new Product(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getDouble("price"),
                                rs.getInt("stock_quantity"),
                                rs.getTimestamp("created_at")
                        )
                )
                .optional();
    }

    public void update(Product product) {
        jdbcClient.sql(UPDATE_QUERY)
                .param("name", product.getName())
                .param("price", product.getPrice())
                .param("stock_quantity", product.getStockQuantity())
                .param("id", product.getId())
                .update();
    }

    public Optional<Long> getProductCount() {
        return jdbcClient.sql("select count(*) from products")
                .query(Long.class)
                .optional();
    }

    public List<Product> getAsPage(int pageSize, long offset) {
        return jdbcClient.sql(PRODUCT_PAGABLE_QUERY)
                .param("limit", pageSize)
                .param("offset", offset)
                .query(Product.class)
                .list();
    }

    public void restoreProductStock(Integer productId, Integer quantity) {
        jdbcClient.sql(RESTORE_PRODUCT_STOCK_QUERY)
                .param("quantity", quantity)
                .param("id", productId)
                .update();
    }
}
