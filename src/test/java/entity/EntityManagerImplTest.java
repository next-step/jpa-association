package entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.CustomJpaRepository;
import persistence.entity.EntityInformation;
import persistence.entity.EntityLoader;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;
import persistence.entity.EntityPersister;
import persistence.entity.PersistenceContext;
import persistence.entity.PersistenceContextImpl;
import persistence.entity.SimpleSnapshotStorage;
import persistence.entity.exception.ObjectNotFoundException;
import persistence.sql.ddl.DdlQueryBuilder;
import persistence.sql.ddl.view.mysql.MySQLPrimaryKeyResolver;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.entity.Person;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityManagerImplTest {

    private static DatabaseServer server;
    private static EntityManager entityManager;
    private static PersistenceContext persistenceContext;
    private static CustomJpaRepository<Person, Long> repository;

    @BeforeAll
    static void initDatabase() throws SQLException {
        server = new H2();
        server.start();
        DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(new MySQLPrimaryKeyResolver());
        Connection connection = server.getConnection();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        jdbcTemplate.execute(ddlQueryBuilder.createQuery(Person.class));
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, new InsertQueryBuilder(), new UpdateQueryBuilder(), new DeleteQueryBuilder());
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, new SelectQueryBuilder());
        persistenceContext = new PersistenceContextImpl(new SimpleSnapshotStorage());
        entityManager = new EntityManagerImpl(entityPersister, entityLoader, persistenceContext);
        repository = new CustomJpaRepository<>(entityManager, new EntityInformation());
    }

    @AfterAll
    static void destroy() {
        server.stop();
    }

    @Test
    @DisplayName("entity manager integration test")
    void should_remove_entity() {
        Long id = 1L;
        Person person = new Person(id, "cs", 29, "katd216@gmail.com", 0);
        entityManager.persist(person);
        Person foundPerson = entityManager.find(Person.class, id);
        entityManager.remove(foundPerson);

        assertThatThrownBy(() -> entityManager.find(Person.class, id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 result, got 0");
    }

    @Test
    void should_update_entity() {
        Long id = 2L;
        String updateName = "test";
        Integer updateAge = 32;
        String updateEmail = "katd6@naver.com";
        Person person = new Person(id, "cs", 29, "katd216@gmail.com", 0);
        entityManager.persist(person);
        entityManager.merge(new Person(id, updateName, updateAge, updateEmail, 2));

        Person updatePerson = entityManager.find(Person.class, id);

        assertAll(
                () -> validateFieldValue(Person.class, "name", updateName, updatePerson),
                () -> validateFieldValue(Person.class, "age", updateAge, updatePerson),
                () -> validateFieldValue(Person.class, "email", updateEmail, updatePerson)
        );
    }

    private void validateFieldValue(Class<?> clazz, String fieldName, Object fieldValue, Object instance) throws IllegalAccessException, NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        assertThat(field.get(instance)).isEqualTo(fieldValue);
    }

    @Test
    void should_cache_entity() {
        Long id = 3L;
        Person person = new Person(id, "cs", 29, "katd216@gmail.com", 0);
        entityManager.merge(person);
        Person cacheEntity = entityManager.find(Person.class, id);

        assertThat(cacheEntity).isEqualTo(person);
    }

    @Test
    void should_remove_cache() {
        Long id = 4L;
        Person person = new Person(id, "cs", 29, "katd216@gmail.com", 0);
        entityManager.merge(person);
        entityManager.remove(person);

        assertThat(persistenceContext.getEntity(person.getClass(), id)).isNull();
    }

    @Test
    void should_dirty_check_entity() {
        Long id = 5L;
        Person person = new Person(id, "cs", 29, "katd216@gmail.com", 0);
        Person newPerson = new Person(id, "newPerson", 32, "katd6@naver.com", 1);
        entityManager.merge(person);


        assertThat(persistenceContext.isDirty(newPerson)).isTrue();
        entityManager.merge(newPerson);
        assertThat(persistenceContext.getEntity(person.getClass(), id)).isEqualTo(newPerson);
    }
}
