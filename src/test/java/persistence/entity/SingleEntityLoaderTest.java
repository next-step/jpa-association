package persistence.entity;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.JdbcServerDmlQueryTestSupport;
import persistence.OrderFixtureFactory;
import persistence.PersonV3FixtureFactory;
import persistence.entity.Proxy.CglibProxyFactory;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SingleEntityLoader;
import persistence.model.PersistentClassMapping;
import persistence.sql.Order;
import persistence.sql.ddl.PersonV3;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.DefaultDmlQueryBuilder;
import persistence.sql.mapping.TableBinder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

class SingleEntityLoaderTest extends JdbcServerDmlQueryTestSupport {

    private final TableBinder tableBinder = new TableBinder();
    private final Dialect dialect = new H2Dialect();
    private final DefaultDmlQueryBuilder dmlQueryBuilder = new DefaultDmlQueryBuilder(dialect);

    private final EntityLoader entityLoader = new SingleEntityLoader(tableBinder, PersistentClassMapping.getCollectionPersistentClassBinder(), new CglibProxyFactory(), dmlQueryBuilder, jdbcTemplate);

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("delete from users");
        jdbcTemplate.execute("delete from eager_order_items");
        jdbcTemplate.execute("delete from lazy_order_items");
        jdbcTemplate.execute("delete from orders");
    }

    @DisplayName("클래스 정보로 엔티티를 조회한다.")
    @Test
    public void load() throws Exception {
        // given
        final Class<PersonV3> clazz = PersonV3.class;
        final long key = 1L;
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        final String insertQuery = generateUserTableStubInsertQuery(person);

        jdbcTemplate.execute(insertQuery);

        // when
        final PersonV3 entity = entityLoader.load(clazz, key).get(0);

        // then
        assertThat(entity).isNotNull()
                .extracting("id", "name", "age", "email")
                .contains(key, person.getName(), person.getAge(), person.getEmail());
    }

    @DisplayName("EAGER 연관관계 클래스 정보로 엔티티를 조회한다")
    @Test
    public void loadEagerJoin() throws Exception {
        // given
        final Class<Order> clazz = Order.class;
        final long key = 4L;
        final Order order1 = OrderFixtureFactory.generateOrderStub(key);
        final Order order2 = OrderFixtureFactory.generateOrderStub(5L, List.of(), List.of());
        final Order order3 = OrderFixtureFactory.generateOrderStub(6L, List.of(), List.of());
        final String order1InsertQuery = generateOrderTableStubInsertQuery(order1);
        final String order2InsertQuery = generateOrderTableStubInsertQuery(order2);
        final String order3InsertQuery = generateOrderTableStubInsertQuery(order3);
        final String orderItemInsertQuery = generateEagerOrderItemTableStubInsertQuery(order1);

        jdbcTemplate.execute(order1InsertQuery);
        jdbcTemplate.execute(order2InsertQuery);
        jdbcTemplate.execute(order3InsertQuery);
        jdbcTemplate.execute(orderItemInsertQuery);

        // when
        final List<Order> results = entityLoader.load(clazz, null);

        // then
        assertAll(
                () -> assertThat(results).hasSize(3).extracting("id", "orderNumber")
                        .containsExactlyInAnyOrder(
                                tuple(order1.getId(), order1.getOrderNumber()),
                                tuple(order2.getId(), order2.getOrderNumber()),
                                tuple(order3.getId(), order3.getOrderNumber())
                        ),
                () ->assertThat(results.get(0).getEagerOrderItems()).hasSize(order1.getEagerOrderItems().size())
                        .extracting("id", "product", "quantity")
                        .containsExactlyInAnyOrder(
                                results.get(0).getEagerOrderItems().stream().map(orderItem -> tuple(orderItem.getId(), orderItem.getProduct(), orderItem.getQuantity())).toArray(Tuple[]::new)
                        ),
                () -> assertThat(results.get(1).getEagerOrderItems()).hasSize(0),
                () -> assertThat(results.get(2).getEagerOrderItems()).hasSize(0)
        );
    }

    @DisplayName("EAGER 와 LAZY 연관관계가 모두 있는 클래스 정보로 엔티티를 조회한다")
    @Test
    public void loadEagerAndLazyJoin() throws Exception {
        // given
        final Class<Order> clazz = Order.class;
        final long key = 1L;
        final Order order1 = OrderFixtureFactory.generateOrderStub(key);
        final Order order2 = OrderFixtureFactory.generateOrderStub(2L, OrderFixtureFactory.generateEagerOrderItemsStub(4L, 5L), List.of());
        final Order order3 = OrderFixtureFactory.generateOrderStub(3L, List.of(), OrderFixtureFactory.generateLazyItemsStub(4L));
        final Order[] orders = new Order[]{order1, order2, order3};
        final String order1InsertQuery = generateOrderTableStubInsertQuery(order1);
        final String order2InsertQuery = generateOrderTableStubInsertQuery(order2);
        final String order3InsertQuery = generateOrderTableStubInsertQuery(order3);
        final String eagerOrderItemInsertQuery1 = generateEagerOrderItemTableStubInsertQuery(order1);
        final String eagerOrderItemInsertQuery2 = generateEagerOrderItemTableStubInsertQuery(order2);
        final String lazyOrderItemInsertQuery1 = generateLazyOrderItemTableStubInsertQuery(order1);
        final String lazyOrderItemInsertQuery2 = generateLazyOrderItemTableStubInsertQuery(order3);

        jdbcTemplate.execute(order1InsertQuery);
        jdbcTemplate.execute(order2InsertQuery);
        jdbcTemplate.execute(order3InsertQuery);
        jdbcTemplate.execute(eagerOrderItemInsertQuery1);
        jdbcTemplate.execute(eagerOrderItemInsertQuery2);
        jdbcTemplate.execute(lazyOrderItemInsertQuery1);
        jdbcTemplate.execute(lazyOrderItemInsertQuery2);

        // when
        final List<Order> results = entityLoader.load(clazz, null);

        // then
        assertAll(
                () -> assertThat(results).hasSize(3).extracting("id", "orderNumber")
                        .containsExactlyInAnyOrder(
                                tuple(order1.getId(), order1.getOrderNumber()),
                                tuple(order2.getId(), order2.getOrderNumber()),
                                tuple(order3.getId(), order3.getOrderNumber())
                        ),
                () -> {
                    for (int i = 0; i < results.size(); i++) {
                        final Order result = results.get(i);
                        final Order order = orders[i];

                        assertThat(result.getEagerOrderItems()).hasSize(order.getEagerOrderItems().size())
                                .extracting("id", "product", "quantity")
                                .containsExactlyInAnyOrder(
                                        result.getEagerOrderItems().stream().map(orderItem -> tuple(orderItem.getId(), orderItem.getProduct(), orderItem.getQuantity())).toArray(Tuple[]::new)
                                );
                        assertThat(result.getLazyOrderItems()).hasSize(order.getLazyOrderItems().size())
                                .extracting("id", "product", "quantity")
                                .containsExactlyInAnyOrder(
                                        result.getLazyOrderItems().stream().map(orderItem -> tuple(orderItem.getId(), orderItem.getProduct(), orderItem.getQuantity())).toArray(Tuple[]::new)
                                );
                    }
                }
        );
    }

}
