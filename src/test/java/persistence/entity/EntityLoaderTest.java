package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Order;
import domain.OrderItem;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.Person;
import persistence.sql.column.Columns;
import persistence.sql.column.IdColumn;
import persistence.sql.column.TableColumn;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.MysqlDialect;
import persistence.sql.dml.SelectQueryBuilder;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityLoaderTest {

    private JdbcTemplate jdbcTemplate;
    private TableColumn table;
    private Dialect dialect;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseServer server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        Class<Person> personEntity = Person.class;
        table = new TableColumn(personEntity);
        dialect = new MysqlDialect();
        Columns columns = new Columns(personEntity.getDeclaredFields());
        IdColumn idColumn = new IdColumn(personEntity.getDeclaredFields());

        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(table, columns, idColumn, dialect);

        String createQuery = createQueryBuilder.build();
        jdbcTemplate.execute(createQuery);
    }

    @AfterEach
    void tearDown() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(table);
        String dropQuery = dropQueryBuilder.build();
        jdbcTemplate.execute(dropQuery);
    }

    @DisplayName("find 메서드를 통해 id에 해당하는 Person 객체를 찾는다.")
    @Test
    void find() {
        // given
        Person person = new Person("홍길동", "jon@test.com", 20);
        EntityManager entityManager = new EntityManagerImpl(jdbcTemplate, dialect);
        entityManager.persist(person);
        EntityLoader entityLoader = new EntityLoaderImpl(jdbcTemplate, dialect);

        // when
        Person foundPerson = entityLoader.find(Person.class, 1L);

        // then
        assertAll(
                () -> assertEquals(person.getName(), foundPerson.getName()),
                () -> assertEquals(person.getEmail(), foundPerson.getEmail())
        );
    }

    @DisplayName("연관된 엔티티를 조회해서 엔티티화 시킨다.")
    @Test
    void entity() {
        //given
        jdbcTemplate.execute("create table orders (id bigint auto_increment, order_number varchar(255), primary key (id))");
        jdbcTemplate.execute("create table order_items (id bigint auto_increment, product varchar(255), quantity int, order_id bigint, FOREIGN KEY (order_id) REFERENCES orders(id), primary key (id))");

        jdbcTemplate.execute("insert into orders (id, order_number) values (1, 'order1')");
        jdbcTemplate.execute("insert into order_items (id, product, quantity, order_id) values (1, 'product1', 1, 1)");
        jdbcTemplate.execute("insert into order_items (id, product, quantity, order_id) values (2, 'product2', 2, 1)");

        //when
        EntityLoader entityLoader = new EntityLoaderImpl(jdbcTemplate, dialect);
        Order order = entityLoader.find(Order.class, 1L);

        //then
        assertAll(
                () -> assertThat(order.getOrderNumber()).isEqualTo("order1"),
                () -> assertThat(order.getOrderItems()).hasSize(2),
                () -> assertThat(order.getOrderItems().get(0).getId()).isEqualTo(1L),
                () -> assertThat(order.getOrderItems().get(0).getProduct()).isEqualTo("product1"),
                () -> assertThat(order.getOrderItems().get(0).getQuantity()).isEqualTo(1),
                () -> assertThat(order.getOrderItems().get(1).getId()).isEqualTo(2L),
                () -> assertThat(order.getOrderItems().get(1).getProduct()).isEqualTo("product2"),
                () -> assertThat(order.getOrderItems().get(1).getQuantity()).isEqualTo(2)
        );
    }

}
