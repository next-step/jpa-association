package persistence.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.H2DBTestSupport;
import persistence.Order;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

class CollectionLoaderTest extends H2DBTestSupport {
    private final CollectionLoader collectionLoader = new CollectionLoader(
            jdbcTemplate,
            TableData.from(Order.class),
            Columns.createColumns(Order.class)
    );

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("create table orders (id bigint auto_increment, order_number varchar(255))");
        jdbcTemplate.execute("create table order_items (id bigint auto_increment, order_id bigint, product varchar(255), quantity int)");
    }

    @AfterEach
    void clear() {
        jdbcTemplate.execute("drop table orders");
        jdbcTemplate.execute("drop table order_items");
    }

    @Test
    @DisplayName("oneToMany eager 로딩 테스트")
    void eagerLoadTest() {
        jdbcTemplate.execute("insert into orders (id, order_number) values (1, '1')");

        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product1', 1)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product2', 2)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product3', 3)");

        Order order = collectionLoader.load(Order.class, 1L);

        assertSoftly(softly -> {
            softly.assertThat(order.getId()).isEqualTo(1L);
            softly.assertThat(order.getOrderItems()).hasSize(3);
        });
    }
}
