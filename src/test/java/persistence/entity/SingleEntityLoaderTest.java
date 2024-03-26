package persistence.entity;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.JdbcServerDmlQueryTestSupport;
import persistence.OrderFixtureFactory;
import persistence.PersonV3FixtureFactory;
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

class SingleEntityLoaderTest extends JdbcServerDmlQueryTestSupport {

    private final TableBinder tableBinder = new TableBinder();
    private final Dialect dialect = new H2Dialect();
    private final DefaultDmlQueryBuilder dmlQueryBuilder = new DefaultDmlQueryBuilder(dialect);

    private final EntityLoader entityLoader = new SingleEntityLoader(tableBinder, PersistentClassMapping.getCollectionPersistentClassBinder(), dmlQueryBuilder, jdbcTemplate);

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
        final long key = 1L;
        final Order order1 = OrderFixtureFactory.generateOrderStub(key);
        final Order order2 = OrderFixtureFactory.generateOrderStub(2L, List.of());
        final Order order3 = OrderFixtureFactory.generateOrderStub(3L, List.of());
        final String order1InsertQuery = generateOrderTableStubInsertQuery(order1);
        final String order2InsertQuery = generateOrderTableStubInsertQuery(order2);
        final String order3InsertQuery = generateOrderTableStubInsertQuery(order3);
        final String orderItemInsertQuery = generateOrderItemTableStubInsertQuery(order1);

        jdbcTemplate.execute(order1InsertQuery);
        jdbcTemplate.execute(order2InsertQuery);
        jdbcTemplate.execute(order3InsertQuery);
        jdbcTemplate.execute(orderItemInsertQuery);

        // when
        final List<Order> results = entityLoader.load(clazz, null);

        // then
        assertThat(results).hasSize(3).extracting("id", "orderNumber")
                .containsExactlyInAnyOrder(
                        tuple(order1.getId(), order1.getOrderNumber()),
                        tuple(order2.getId(), order2.getOrderNumber()),
                        tuple(order3.getId(), order3.getOrderNumber())
                );
        assertThat(results.get(0).getOrderItems()).hasSize(order1.getOrderItems().size())
                .extracting("id", "product", "quantity")
                .containsExactlyInAnyOrder(
                        order1.getOrderItems().stream().map(orderItem -> tuple(orderItem.getId(), orderItem.getProduct(), orderItem.getQuantity())).toArray(Tuple[]::new)
                );
        assertThat(results.get(1).getOrderItems()).hasSize(0);
        assertThat(results.get(2).getOrderItems()).hasSize(0);
    }

}
