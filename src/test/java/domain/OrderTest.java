package domain;

import database.dialect.H2Dialect;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.support.DatabaseSetup;

@DatabaseSetup
class OrderTest {

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        String orderCreateQuery = "CREATE TABLE orders (id BIGINT AUTO_INCREMENT PRIMARY KEY, orderNumber VARCHAR(255));";
        String orderItemCreateQuery = "CREATE TABLE order_items (id BIGINT AUTO_INCREMENT PRIMARY KEY, product VARCHAR(255), quantity INT, order_id BIGINT, FOREIGN KEY (order_id) REFERENCES orders(id));";
        jdbcTemplate.execute(orderCreateQuery);
        jdbcTemplate.execute(orderItemCreateQuery);
    }

    @AfterEach
    void tearDown(JdbcTemplate jdbcTemplate) {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(new H2Dialect());
        String orderDropQuery = dropQueryBuilder.build(Order.class);
        String orderItemDropQuery = dropQueryBuilder.build(OrderItem.class);
        jdbcTemplate.execute(orderItemDropQuery);
        jdbcTemplate.execute(orderDropQuery);
    }


    @Test
    void find_order() {

    }
}
