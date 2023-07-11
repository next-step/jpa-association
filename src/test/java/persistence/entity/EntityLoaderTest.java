package persistence.entity;

import database.H2;
import jdbc.JdbcTemplate;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.builder.InsertQueryBuilder;

import java.sql.Connection;
import java.sql.SQLException;

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
        String insertQuery = InsertQueryBuilder.INSTANCE.insert(person);
        jdbcTemplate.execute(insertQuery);

        // when
        Person result = entityLoader.load(Person.class, 1L);

        // then
        assertThat(person.getName()).isEqualTo(result.getName());
    }
}