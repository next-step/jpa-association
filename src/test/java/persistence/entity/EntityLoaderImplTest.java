package persistence.entity;

import database.H2;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityLoaderImplTest extends JpaTest {

    static Person3 person = new Person3(1L, "test", 20, "test@test.com");
    static EntityMetaData entityMetaData;

    @BeforeAll
    static void init() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        entityMetaData = new EntityMetaData(Person3.class, person);
        initForTest(entityMetaData);
    }

    @BeforeEach
    void setUp() {
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

    @DisplayName("findById 테스트")
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
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(dialect, entityMetaData);
        jdbcTemplate.execute(createQueryBuilder.createTable(person));
    }

    private void dropTable() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(entityMetaData);
        jdbcTemplate.execute(dropQueryBuilder.dropTable());
    }
}
