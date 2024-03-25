package persistence.entity.entitymanager;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.EntityManagerImpl;
import persistence.entity.testfixture.order.Order;
import persistence.entity.testfixture.order.OrderItem;

import java.util.List;
import java.util.Optional;

public class AssociationEntityManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(AssociationEntityManagerTest.class);
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    private EntityManager entityManager;

    @BeforeAll
    static void setupOnce() {
        try {
            server = new H2();
            server.start();
            jdbcTemplate = new JdbcTemplate(server.getConnection());
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

    @BeforeEach
    void setUp() {
       entityManager = new EntityManagerImpl(jdbcTemplate);
    }

    @Test
    void find_entity시_연관관계가_있는_엔티티도_반환된다_eager() {
        // given
        jdbcTemplate.execute("CREATE TABLE orders (id BIGINT AUTO_INCREMENT PRIMARY KEY, order_number VARCHAR(30) NULL)");
        jdbcTemplate.execute("CREATE TABLE order_items (id BIGINT AUTO_INCREMENT PRIMARY KEY, product VARCHAR(30) NULL, quantity INT NULL, order_id BIGINT, FOREIGN KEY (order_id) REFERENCES orders(id))");

        jdbcTemplate.execute("INSERT INTO orders (order_number) VALUES ('ON1000')");
        jdbcTemplate.execute("INSERT INTO order_items (product, quantity, order_id) VALUES ('불닭볶음면', 10, 1)");
        jdbcTemplate.execute("INSERT INTO order_items (product, quantity, order_id) VALUES ('마라볶음면', 7, 1)");


        Order order = entityManager.find(Order.class, 1L).get();

        List<OrderItem> orderItemsResult = List.of(new OrderItem("불닭볶음면", 10), new OrderItem("마라볶음면", 7));
        Assertions.assertThat(order).isEqualTo(new Order(1L, "ON1000", orderItemsResult));
    }
}
