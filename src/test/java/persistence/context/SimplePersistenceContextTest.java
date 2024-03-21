package persistence.context;

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
import persistence.entity.EntitySnapshot;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import pojo.EntityMetaData;
import pojo.EntityStatus;
import pojo.FieldInfos;
import pojo.IdField;

import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SimplePersistenceContextTest extends JpaTest {

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

    @DisplayName("persist 후 조회 시 cachedSnapshot 과 일치 여부 확인")
    @Test
    void addEntityAndGetCachedDatabaseSnapshotTest() {
        insertData();
        findData();

        EntitySnapshot cachedDatabaseSnapshot = persistenceContext.getDatabaseSnapshot(person.getId(), person);

        Field field = new FieldInfos(person.getClass().getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, person);

        assertEquals(cachedDatabaseSnapshot.getMap().get(idField.getFieldNameData()), Long.toString(person.getId()));
    }

    @DisplayName("EntityStatus 확인 - save and find")
    @Test
    void saveAndFindEntityStatusTest() {
        insertData();
        assertThat(entityEntry.getEntityStatus()).isEqualTo(EntityStatus.MANAGED);

        findData();
        assertThat(entityEntry.getEntityStatus()).isEqualTo(EntityStatus.MANAGED);
    }

    @DisplayName("EntityStatus 확인 - gone 상태가 된 entity 조회 시 오류")
    @Test
    void removeAndFindEntityStatusExceptionTest() {
        insertData();
        removeData();

        assertThatThrownBy(this::findData).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(this::findData).hasMessageContaining("object not found exception");
    }

    @DisplayName("EntityStatus 확인 - update")
    @Test
    void updateEntityStatusTest() {
        insertData();
        updateData(new Person3(person.getId(), "tester", 50, "tester@test.com"));
        assertThat(entityEntry.getEntityStatus()).isEqualTo(EntityStatus.MANAGED);
    }

    @DisplayName("EntityStatus 확인 - readOnly 시 update 수행 오류")
    @Test
    void updateEntityStatusExceptionTest() {
        insertData();
        entityEntry.preReadOnly();
        assertThatThrownBy(() -> updateData(new Person3(person.getId(), "tester", 50, "tester@test.com")))
                .isInstanceOf(IllegalStateException.class);
        entityEntry.preFind();
    }

    @DisplayName("EntityStatus 확인 - remove")
    @Test
    void removeEntityStatusTest() {
        insertData();
        removeData();
        assertThat(entityEntry.getEntityStatus()).isEqualTo(EntityStatus.GONE);
    }

    private void createTable() {
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(dialect, entityMetaData);
        jdbcTemplate.execute(createQueryBuilder.createTable(person));
    }

    private Person3 findData() {
        return simpleEntityManager.find(person, person.getClass(), person.getId());
    }

    private void insertData() {
        simpleEntityManager.persist(person);
    }

    private void updateData(Person3 updatedPerson) {
        simpleEntityManager.update(updatedPerson);
    }

    private void removeData() {
        simpleEntityManager.remove(person);
    }

    private void dropTable() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(entityMetaData);
        jdbcTemplate.execute(dropQueryBuilder.dropTable());
    }
}
