package persistence.entity;

import org.junit.jupiter.api.*;
import persistence.H2DBTestSupport;
import persistence.Order;
import persistence.OrderLazy;
import persistence.sql.mapping.Associations;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class CollectionLoaderTest extends H2DBTestSupport {
    private final CollectionLoader eagerCollectionLoader = new CollectionLoader(
            jdbcTemplate,
            TableData.from(Order.class),
            Columns.createColumns(Order.class),
            Associations.fromEntityClass(Order.class)
    );

    private final CollectionLoader lazyCollectionLoader = new CollectionLoader(
            jdbcTemplate,
            TableData.from(OrderLazy.class),
            Columns.createColumns(OrderLazy.class),
            Associations.fromEntityClass(OrderLazy.class)
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

        Order order = eagerCollectionLoader.load(Order.class, 1L);

        assertSoftly(softly -> {
            softly.assertThat(order.getId()).isEqualTo(1L);
            softly.assertThat(order.getOrderItems()).hasSize(3);
        });
    }

    @Test
    @DisplayName("lazy 로딩이 있을땐 persistentCollection 을 반환한다")
    void instanceIsProxyWhenHasLazyLoading() {
        jdbcTemplate.execute("insert into orders (id, order_number) values (1, '1')");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product1', 1)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product2', 2)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product3', 3)");

        OrderLazy order = lazyCollectionLoader.load(OrderLazy.class, 1L);

        assertThat(order.getOrderItems()).isInstanceOf(PersistentList.class);
    }

    @Test
    @DisplayName("lazy froxy는 접근시 쿼리를 통해 로딩한다")
    @Disabled
    void getDataWhenAccessCollection() {
        jdbcTemplate.execute("insert into orders (id, order_number) values (1, '1')");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (1, 'product1', 1)");

        OrderLazy order = eagerCollectionLoader.load(OrderLazy.class, 1L);

        assertSoftly(softly -> {
            softly.assertThat(order.getOrderItems().get(0).getId()).isEqualTo(1L);
            softly.assertThat(order.getOrderItems().get(0).getProduct()).isEqualTo("product1");
        });
    }
}
