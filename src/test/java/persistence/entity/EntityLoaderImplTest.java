package persistence.entity;

import database.H2;
import entity.Order;
import entity.Person3;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.JpaTest;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import pojo.EntityMetaData;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityLoaderImplTest extends JpaTest {

    static EntityMetaData entityMetaData;

    @BeforeAll
    static void init() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
    }

    @BeforeEach
    void setUp() {
        entityMetaData = new EntityMetaData(Person3.class);
        initForTest(entityMetaData);
        createTable();
    }

    @AfterEach
    void remove() {
        dropTable();
    }

    @AfterAll
    static void destroy() {
        server.stop();
    }

    @DisplayName("findById 테스트 - 연관관계가 없는 경우")
    @Test
    void findByIdTest() {
        entityPersister.insert(person);
        Person3 person3 = entityLoader.findById(person.getClass(), person, person.getId());
        assertAll(
                () -> assertThat(person3.getId()).isEqualTo(person.getId()),
                () -> assertThat(person3.getName()).isEqualTo(person.getName()),
                () -> assertThat(person3.getAge()).isEqualTo(person.getAge()),
                () -> assertThat(person3.getEmail()).isEqualTo(person.getEmail())
        );
    }

    @DisplayName("findById 테스트 - 연관관계가 있는 경우")
    @Test
    void findByIdWithAssociationTest() {
        entityMetaData = new EntityMetaData(Order.class);
        initForTest(entityMetaData);

        createOrderAndOrderItemTable();
        insertOrderAndOrderItemData();

        List<? extends Order> savedOrderList = entityLoader.findByIdWithAssociation(order.getClass(), order, order.getId());
        assertThat(savedOrderList).hasSize(3);

        dropOrderAndOrderItemTable();
    }

    @DisplayName("findAll 테스트")
    @Test
    void findAllTest() {
        entityPersister.insert(person);

        person = new Person3(2L, "test2", 22, "test2@test.com");
        entityPersister.insert(person);

        person = new Person3(3L, "test3", 23, "test3@test.com");
        entityPersister.insert(person);

        assertThat(entityLoader.findAll(person.getClass())).hasSize(3);
    }

    private void createTable() {
        dropTable();
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(dialect, entityMetaData);
        jdbcTemplate.execute(createQueryBuilder.createTable(person));
    }

    private void dropTable() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(entityMetaData);
        jdbcTemplate.execute(dropQueryBuilder.dropTable());
    }
}
