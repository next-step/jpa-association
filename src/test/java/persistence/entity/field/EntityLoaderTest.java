package persistence.entity.field;

import database.H2;
import jdbc.JdbcTemplate;
import model.LazyOrder;
import model.Order;
import model.OrderItem;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.manager.EntityLoader;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;
import persistence.sql.dml.builder.InsertQueryBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static fixture.PersonFixtures.createPerson;
import static fixture.TableFixtures.createTable;
import static fixture.TableFixtures.dropTable;
import static org.assertj.core.api.Assertions.assertThat;

class EntityLoaderTest {

    private EntityLoader entityLoader;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws SQLException {
        Connection connection = new H2().getConnection();
        jdbcTemplate = new JdbcTemplate(connection);
        entityLoader = new EntityLoader(jdbcTemplate);
        createTable(Person.class, jdbcTemplate);
    }

    @AfterEach
    void clear() {
        dropTable(Person.class, jdbcTemplate);
    }

    @Test
    @DisplayName("엔티티를 조회한다")
    void load() {
        // given
        Person person = createPerson();
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(person.getClass());
        String insertQuery = InsertQueryBuilder.INSTANCE.insert(entityMeta, person);
        jdbcTemplate.execute(insertQuery);

        // when
        Person result = entityLoader.load(entityMeta, 1L);

        // then
        assertThat(person.getName()).isEqualTo(result.getName());
    }

    @Test
    @DisplayName("OneToMany 즉시로딩 연관관계를 가진 엔티티를 조회한다")
    void oneToManyEagerLoad() {
        // given
        jdbcTemplate.execute("create table orders (id bigint primary key auto_increment, order_number varchar(255))");
        jdbcTemplate.execute("insert into orders (order_number) values ('order_number1')");
        jdbcTemplate.execute("insert into orders (order_number) values ('order_number2')");

        jdbcTemplate.execute("create table order_items (id bigint primary key auto_increment, order_id bigint, product varchar(255), quantity integer)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (2, 'product1', 1)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (2, 'product2', 1)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (2, 'product3', 1)");

        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(Order.class);


        // when
        Order result = entityLoader.load(entityMeta, 2L);

        // then
        assertThat(result.getOrderItems().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("OneToMany 지연로딩 연관관계를 가진 엔티티를 조회한다")
    void oneToManyLazyLoad() {
        // given
        jdbcTemplate.execute("create table orders (id bigint primary key auto_increment, order_number varchar(255))");
        jdbcTemplate.execute("insert into orders (order_number) values ('order_number1')");
        jdbcTemplate.execute("insert into orders (order_number) values ('order_number2')");

        jdbcTemplate.execute("create table order_items (id bigint primary key auto_increment, order_id bigint, product varchar(255), quantity integer)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (2, 'product1', 1)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (2, 'product2', 1)");
        jdbcTemplate.execute("insert into order_items (order_id, product, quantity) values (2, 'product3', 1)");

        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(LazyOrder.class);


        // when
        LazyOrder result = entityLoader.load(entityMeta, 2L);

        // then
        List<OrderItem> orderItems = result.getOrderItems();
        int size = orderItems.size();
        assertThat(size).isEqualTo(3);
    }
}