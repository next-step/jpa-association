package persistence.entity.persister;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.entity.EntityKey;
import persistence.fake.FakeDialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.NoAutoIncrementPerson;
import persistence.testFixtures.Person;

class EntityPersisterTest {

    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;

    private Dialect dialect;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
        dialect = new FakeDialect();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).create());
    }

    @Test
    @DisplayName("자동 증가 칼럼을 가진 엔티티가 저장이 된다.")
    void entityAutoIncrementSave() {
        Person person = new Person("이름", 3, "dsa@gmil.com");

        SimpleEntityPersister entityPersister = SimpleEntityPersister.create(jdbcTemplate,
                QueryGenerator.of(Person.class, dialect), EntityMeta.from(Person.class));

        final Person result = entityPersister.insert(person);

        assertSoftly(it -> {
            it.assertThat(result.getId()).isNotNull();
            it.assertThat(result).isEqualTo(person);
        });
    }

    @Test
    @DisplayName("엔티티가 저장이 된다.")
    void entitySave() {
        //given
        NoAutoIncrementPerson person = new NoAutoIncrementPerson(3L, "이름", 3, "dsa@gmil.com");

        SimpleEntityPersister entityPersister = SimpleEntityPersister.create(jdbcTemplate
                , QueryGenerator.of(Person.class, dialect)
                , EntityMeta.from(NoAutoIncrementPerson.class));

        //when
        final NoAutoIncrementPerson result = entityPersister.insert(person);

        //then
        assertSoftly(it -> {
            it.assertThat(result.getId()).isEqualTo(3L);
            it.assertThat(result).isEqualTo(person);
        });
    }

    @Test
    @DisplayName("엔티티가 업데이트가 된다.")
    void entityUpdate() {
        //given
        Person person = new Person(1L, "이름", 3, "dsa@gmil.com");
        SimpleEntityPersister entityPersister = SimpleEntityPersister.create(jdbcTemplate
                , QueryGenerator.of(Person.class, dialect)
                , EntityMeta.from(Person.class));

        //when & then
        assertThat(entityPersister.update(person)).isTrue();
    }


    @Test
    @DisplayName("엔티티가 삭제 된다.")
    void entityDeleteByKey() {
        //given
        SimpleEntityPersister entityPersister = SimpleEntityPersister.create(jdbcTemplate
                , QueryGenerator.of(Person.class, dialect)
                , EntityMeta.from(Person.class)
        );
        Person person = new Person(1L, "이름", 3, "dsa@gmil.com");

        //then
        Assertions.assertDoesNotThrow(() -> {
            entityPersister.deleteByKey(EntityKey.of(person));
        });
    }


    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).drop());
        server.stop();
    }
}
