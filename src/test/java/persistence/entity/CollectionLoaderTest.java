package persistence.entity;

import org.junit.jupiter.api.*;
import persistence.H2DBTestSupport;
import persistence.OrderLazy;
import persistence.sql.mapping.Associations;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionLoaderTest extends H2DBTestSupport {
    private final CollectionLoader collectionLoader = new CollectionLoader(
            jdbcTemplate,
            Associations.fromEntityClass(OrderLazy.class).getLazyAssociations().get(0)
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
    void testLoadCollection(){
        jdbcTemplate.execute("insert into orders (id, order_number) values (1, '1')");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product1', 1)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product2', 2)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product3', 3)");

        Collection<Object> collection = collectionLoader.loadCollection(1L);

        assertThat(collection.size()).isEqualTo(3);
    }
}
