package persistence.entity;

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

class OneToManyScenarioTest extends H2DatabaseTest {
    private EntityManagerImpl entityManager;

    @BeforeEach
    void setUp() {
        entityManager = EntityManagerImpl.from(jdbcTemplate);

        List<Class<?>> allEntities = List.of(EagerLoadTestOrder.class, EagerLoadTestOrderItem.class,
                                             LazyLoadTestOrder.class, LazyLoadTestOrderItem.class);

        jdbcTemplate.execute("DROP TABLE eagerload_orders IF EXISTS");
        jdbcTemplate.execute("DROP TABLE eagerload_order_items IF EXISTS");
        jdbcTemplate.execute("DROP TABLE lazyload_orders IF EXISTS");
        jdbcTemplate.execute("DROP TABLE lazyload_order_items IF EXISTS");

        jdbcTemplate.execute(new Create(EagerLoadTestOrder.class, allEntities, dialect).buildQuery());
        jdbcTemplate.execute(new Create(EagerLoadTestOrderItem.class, allEntities, dialect).buildQuery());

        jdbcTemplate.execute(new Create(LazyLoadTestOrder.class, allEntities, dialect).buildQuery());
        jdbcTemplate.execute(new Create(LazyLoadTestOrderItem.class, allEntities, dialect).buildQuery());

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
        EagerLoadTestOrder res = entityManager.find(EagerLoadTestOrder.class, 1L);
        assertThat(res.toString()).isEqualTo("Order{id=1, orderNumber='1234', orderItems=[OrderItem{id=1, product='product1', quantity=5}, OrderItem{id=1, product='product20', quantity=50}]}");
    }
}
