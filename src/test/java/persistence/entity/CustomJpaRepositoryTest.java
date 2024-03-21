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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomJpaRepositoryTest extends JpaTest {

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


    @DisplayName("save 시 dirty checking 로직 구현")
    @Test
    void saveWithDirtyTest() {
        jpaRepository.save(person);

        Person3 updatedPerson = new Person3(person.getId(), "test2", 30, "test2@test.com");
        jpaRepository.save(updatedPerson);

        EntitySnapshot cachedDatabaseSnapshot = persistenceContext.getDatabaseSnapshot(person.getId(), person);
        Map<String, Object> map = cachedDatabaseSnapshot.getMap();

        assertEquals(Long.valueOf(String.valueOf(map.get("id"))), updatedPerson.getId());

        simpleEntityManager.remove(person);
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
