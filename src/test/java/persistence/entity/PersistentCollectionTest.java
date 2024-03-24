package persistence.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.H2DBTestSupport;
import persistence.OrderItem;
import persistence.OrderLazy;
import persistence.entity.collection.CollectionLoader;
import persistence.entity.collection.PersistentList;
import persistence.sql.mapping.Associations;

import static org.assertj.core.api.Assertions.assertThat;

class PersistentCollectionTest extends H2DBTestSupport {
    private final CollectionLoader lazyCollectionLoader = new CollectionLoader(
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
    @DisplayName("컬렉션에 접근시 쿼리를 통해 로딩한다")
    void loadWhenAccessCollection() {
        jdbcTemplate.execute("insert into orders (id, order_number) values (1, '1')");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product1', 1)");
        Associations associations = Associations.fromEntityClass(OrderLazy.class);

        PersistentList<OrderItem> list = new PersistentList<>(lazyCollectionLoader, 1L);

        assertThat(list.get(0).getId()).isEqualTo(1L);
    }
}
