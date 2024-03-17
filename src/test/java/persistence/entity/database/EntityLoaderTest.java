package persistence.entity.database;

import database.dialect.MySQLDialect;
import entity.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManagerImpl;
import testsupport.H2DatabaseTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static testsupport.EntityTestUtils.assertSamePerson;

class EntityLoaderTest extends H2DatabaseTest {
    private EntityManagerImpl entityManager;
    private EntityLoader entityLoader;

    @BeforeEach
    void setUp() {
        entityManager = EntityManagerImpl.from(jdbcTemplate, dialect);
        entityLoader = new EntityLoader(jdbcTemplate, MySQLDialect.getInstance());
    }

    @Test
    void loadMissingRecord() {
        assertThat(entityLoader.load(Person.class, 1L)).isEmpty();
    }

    @Test
    void load2() {
        Person person = new Person(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        Person found = entityLoader.load(Person.class, 1L).get();

        assertSamePerson(found, person, false);
    }
}
