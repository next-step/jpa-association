package persistence.entity;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


import database.DatabaseServer;
import database.H2;
import java.util.List;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.fake.FakeDialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;
import persistence.testFixtures.assosiate.Order;
import persistence.testFixtures.assosiate.OrderItem;
import util.InitTestDataLoader;

class EntityMapperTest {

    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;

    private Dialect dialect;


    @BeforeEach
    void setUp() throws Exception {
        server = new H2();
        server.start();
        dialect = new FakeDialect();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        InitTestDataLoader loader = new InitTestDataLoader(jdbcTemplate);
        loader.load("init.SQL");
    }

    @Test
    @DisplayName("엔터티와 ResultSet을 맵핑한다.")
    void resultSetToEntity() {
        //given
        EntityMapper entityMapper = new EntityMapper(EntityMeta.from(Person.class));

        //when
        final Person person = jdbcTemplate.queryForObject(
                QueryGenerator.of(Person.class, dialect).select().findByIdQuery(-1L),
                (rs) -> entityMapper.resultSetToEntity(Person.class, rs));

        //then
        assertSoftly((it) -> {
            it.assertThat(person.getId()).isEqualTo(-1L);
            it.assertThat(person.getName()).isEqualTo("user-1");
            it.assertThat(person.getAge()).isEqualTo(10);
            it.assertThat(person.getEmail()).isEqualTo("userEmail");
        });
    }


    @Test
    @DisplayName("oneToMany 엔터티와 ResultSet을 맵핑한다.")
    void resultSetToOneToManyEntity() {
        //given
        EntityMapper entityMapper = new EntityMapper(EntityMeta.from(Order.class));

        //when
        final Order order = jdbcTemplate.queryForObject(
                QueryGenerator.of(Order.class, dialect).select().findByIdQuery(1L),
                (rs) -> entityMapper.resultSetToEntity(Order.class, rs));

        //then
        assertSoftly((it) -> {
            it.assertThat(order.getId()).isEqualTo(1L);
            it.assertThat(order.getOrderNumber()).isEqualTo("order-number-1");
            it.assertThat(order.getOrderItems()).hasSize(2);
        });
    }

    @Test
    @DisplayName("다건 엔티티를 맵핑한다.")
    void resultSetToOneToManyEntity2() {
        //given
        EntityMapper entityMapper = new EntityMapper(EntityMeta.from(Order.class));

        final String query = QueryGenerator.of(Order.class, dialect).select().findByIdQuery(1L);
        System.out.println(query);

        //when
        final List<Order> orders = jdbcTemplate.queryForAll(
                QueryGenerator.of(Order.class, dialect).select().findAllQuery(),
                (rs) -> entityMapper.resultSetToEntityAll(Order.class, rs));

        //then
        assertSoftly((it) -> {
            it.assertThat(orders).hasSize(2);
            it.assertThat(orders).extracting("id").contains(1L, 2L);
            it.assertThat(orders).extracting("orderNumber").contains("order-number-1", "order-number-2");
            it.assertThat(orders.get(0).getOrderItems()).hasSize(2);
            it.assertThat(orders.get(1).getOrderItems()).hasSize(2);
        });
    }

    @AfterEach
    void cleanUp() throws Exception {
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).drop());
        jdbcTemplate.execute(QueryGenerator.of(OrderItem.class, dialect).drop());
        jdbcTemplate.execute(QueryGenerator.of(Order.class, dialect).drop());
        server.stop();
    }


}
