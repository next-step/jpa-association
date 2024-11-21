package persistence.sql.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.domain.Order;
import persistence.sql.domain.OrderItem;
import persistence.sql.domain.Person;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityPersisterTest {
    private JdbcTemplate jdbcTemplate;
    private EntityPersister entityPersister;
    private EntityLoader entityLoader;
    private Person expectedPerson;
    private DatabaseServer server;

    @BeforeEach
    void init() throws SQLException {
        server = new H2();
        server.start();
        CreateQueryBuilder queryBuilder = new CreateQueryBuilder(Person.class);
        String tableQuery = queryBuilder.createTableQuery(Person.class);

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(tableQuery);

        entityPersister = new EntityPersister(Person.class, server.getConnection());
        entityLoader = new EntityLoader(server.getConnection());

        expectedPerson = new Person("양승인", 20, "rhfpdk12@naver.com.com", 1);
        entityPersister.insert(expectedPerson);
    }


    @Test
    @DisplayName("EntityPersister update구현")
    void update() {
        Person changeEmailPerson = new Person(1L, "양승인", 20, "change@naver.com");
        entityPersister.update(changeEmailPerson);

        Person updatedPerson = entityLoader.loadEntity(Person.class, 1L);

        assertThat(changeEmailPerson.getEmail()).isEqualTo(updatedPerson.getEmail());
    }

    @Test
    @DisplayName("EntityPersister insert구현")
    void insert() {
        Person insertedPerson = entityLoader.loadEntity(Person.class, 1L);

        assertThat(insertedPerson.getEmail()).isEqualTo(expectedPerson.getEmail());

    }

    @Test
    @DisplayName("onetomany관계에서의 insert 구현")
    void insert2() throws SQLException {
        String orderCreateQuery = "CREATE TABLE orders (id BIGINT AUTO_INCREMENT PRIMARY KEY, orderNumber VARCHAR(255) NOT NULL);";
        String orderItemCreateQuery = "CREATE TABLE order_items (id BIGINT AUTO_INCREMENT PRIMARY KEY, product VARCHAR(255) NOT NULL, quantity INT NOT NULL, order_id BIGINT, FOREIGN KEY (order_id) REFERENCES orders(id));";
        jdbcTemplate.execute(orderCreateQuery);
        jdbcTemplate.execute(orderItemCreateQuery);

        entityPersister = new EntityPersister(Order.class, server.getConnection());
        entityLoader = new EntityLoader(server.getConnection());

        Order order = new Order(1L, "농작물_주문번호1");
        OrderItem orderItem1 = new OrderItem(1L, "감자", 3);
        OrderItem orderItem2 = new OrderItem(2L, "고구마", 1);
        order.getOrderItems().add(orderItem1);
        order.getOrderItems().add(orderItem2);
        entityPersister.insert(order);

        String selectQuery = "SELECT * FROM order_items WHERE order_id = 1";
        List<OrderItem> selectResult = jdbcTemplate.query(selectQuery, new EntityRowMapper<>(OrderItem.class));

        assertThat(selectResult).hasSize(2);
        assertThat(selectResult.get(0).getProduct()).isEqualTo("감자");
        assertThat(selectResult.get(0).getQuantity()).isEqualTo(3);
        assertThat(selectResult.get(1).getProduct()).isEqualTo("고구마");
        assertThat(selectResult.get(1).getQuantity()).isEqualTo(1);

        jdbcTemplate.execute("drop table order_items");
        jdbcTemplate.execute("drop table orders");
    }

    @Test
    @DisplayName("EntityPersister delete구현")
    void delete() {
        Person deletePerson = new Person(1L, "양승인", 20, "rhfpdk12@naver.com.com");
        entityPersister.delete(deletePerson);

        assertThatThrownBy(
                () -> entityLoader.loadEntity(Person.class, 1L)
        ).isInstanceOf(RuntimeException.class);
    }

    @AfterEach
    void afterEach() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder();
        String dropTableQuery = dropQueryBuilder.dropTableQuery(Person.class);
        jdbcTemplate.execute(dropTableQuery);
    }

}
