package persistence.entity;

import database.mapping.AllEntities;
import database.sql.ddl.Create;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.testentities.EagerLoadTestOrder;
import persistence.entity.testentities.EagerLoadTestOrderItem;
import persistence.entity.testentities.LazyLoadTestOrder;
import persistence.entity.testentities.LazyLoadTestOrderItem;
import testsupport.H2DatabaseTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OneToManyScenarioTest extends H2DatabaseTest {
    private EntityManagerImpl entityManager;

    @BeforeEach
    void setUp() {
        entityManager = EntityManagerImpl.from(jdbcTemplate, dialect);

        AllEntities.register(EagerLoadTestOrder.class);
        AllEntities.register(EagerLoadTestOrderItem.class);
        AllEntities.register(LazyLoadTestOrder.class);
        AllEntities.register(LazyLoadTestOrderItem.class);

        jdbcTemplate.execute("DROP TABLE eagerload_orders IF EXISTS");
        jdbcTemplate.execute("DROP TABLE eagerload_order_items IF EXISTS");
        jdbcTemplate.execute("DROP TABLE lazyload_orders IF EXISTS");
        jdbcTemplate.execute("DROP TABLE lazyload_order_items IF EXISTS");

        jdbcTemplate.execute(new Create(EagerLoadTestOrder.class, dialect).buildQuery());
        jdbcTemplate.execute(new Create(EagerLoadTestOrderItem.class, dialect).buildQuery());

        jdbcTemplate.execute(new Create(LazyLoadTestOrder.class, dialect).buildQuery());
        jdbcTemplate.execute(new Create(LazyLoadTestOrderItem.class, dialect).buildQuery());

        jdbcTemplate.execute("INSERT INTO eagerload_orders (orderNumber) VALUES (1234)");
        jdbcTemplate.execute("INSERT INTO eagerload_order_items (product, quantity, order_id) VALUES ('product1', 5, 1)");
        jdbcTemplate.execute("INSERT INTO eagerload_order_items (product, quantity, order_id) VALUES ('product20', 50, 1)");

        jdbcTemplate.execute("INSERT INTO lazyload_orders (orderNumber) VALUES (1234)");
        jdbcTemplate.execute("INSERT INTO lazyload_order_items (product, quantity, order_id) VALUES ('product1', 5, 1)");
        jdbcTemplate.execute("INSERT INTO lazyload_order_items (product, quantity, order_id) VALUES ('product20', 50, 1)");

        executedQueries.clear();
    }

    @Test
    @DisplayName("FetchType.EAGER 연관관계를 가진 객체를 가져오기")
    void scenario6() {
        EagerLoadTestOrder order = entityManager.find(EagerLoadTestOrder.class, 1L);

        assertAll(
                () -> assertThat(order)
                        .hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("orderNumber", "1234"),
                ()->assertThat(order.getOrderItems()).hasSize(2),
                () -> assertThat(order.getOrderItems().get(0))
                        .hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("product", "product1")
                        .hasFieldOrPropertyWithValue("quantity", 5),
                () -> assertThat(order.getOrderItems().get(1))
                        .hasFieldOrPropertyWithValue("id", 2L)
                        .hasFieldOrPropertyWithValue("product", "product20")
                        .hasFieldOrPropertyWithValue("quantity", 50),
                () -> assertThat(executedQueries).isEqualTo(List.of(
                        "SELECT t.id, t.orderNumber, a0.order_id, a0.id, a0.product, a0.quantity FROM eagerload_orders t LEFT JOIN eagerload_order_items a0 ON t.id = a0.order_id WHERE t.id = 1"))
        );
    }

    @Test
    @DisplayName("FetchType.LAZY 연관관계를 가진 객체를 가져오기")
    void scenario7() {
        LazyLoadTestOrder res = entityManager.find(LazyLoadTestOrder.class, 1L);
        List<LazyLoadTestOrderItem> orderItems = res.getOrderItems();

        assertAll(
                () -> assertThat(orderItems.size()).isEqualTo(2),
                () -> assertThat(orderItems.get(0))
                        .hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("product", "product1")
                        .hasFieldOrPropertyWithValue("quantity", 5),
                () -> assertThat(orderItems.get(1))
                        .hasFieldOrPropertyWithValue("id", 2L)
                        .hasFieldOrPropertyWithValue("product", "product20")
                        .hasFieldOrPropertyWithValue("quantity", 50),
                () -> assertThat(executedQueries).isEqualTo(List.of(
                        "SELECT t.id, t.orderNumber FROM lazyload_orders t WHERE t.id = 1",
                        "SELECT id, product, quantity FROM lazyload_order_items WHERE order_id = 1"
                ))
        );
    }
}
