package persistence;

import database.DatabaseServer;
import database.H2;
import domain.FixtureEntity.Person;
import extension.EntityMetadataExtension;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.core.PersistenceEnvironment;
import persistence.dialect.h2.H2Dialect;
import persistence.sql.ddl.DdlGenerator;
import persistence.sql.dml.DmlGenerator;
import persistence.util.ReflectionUtils;

import java.sql.SQLException;
import java.util.List;

@ExtendWith(EntityMetadataExtension.class)
public abstract class IntegrationTestEnvironment {
    private DatabaseServer server;
    protected DdlGenerator ddlGenerator;
    protected DmlGenerator dmlGenerator;
    protected EntityMetadata<Person> personEntityMetadata;
    protected JdbcTemplate jdbcTemplate;
    protected PersistenceEnvironment persistenceEnvironment;
    protected List<Person> people;

    @BeforeEach
    void integrationSetUp() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        persistenceEnvironment = new PersistenceEnvironment(server, new H2Dialect());
        ddlGenerator = new DdlGenerator(EntityMetadataProvider.getInstance(), persistenceEnvironment.getDialect());
        dmlGenerator = new DmlGenerator(persistenceEnvironment.getDialect());

        personEntityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(Person.class);
        final String createPersonDdl = ddlGenerator.generateCreateDdl(personEntityMetadata);
        jdbcTemplate.execute(createPersonDdl);
        people = createDummyUsers();
        saveDummyUsers();
    }

    @AfterEach
    void integrationTearDown() {
        final String dropDdl = ddlGenerator.generateDropDdl(personEntityMetadata);
        jdbcTemplate.execute(dropDdl);
        server.stop();
    }

    private static List<Person> createDummyUsers() {
        final Person test00 = new Person("test00", 0, "test00@gmail.com");
        final Person test01 = new Person("test01", 10, "test01@gmail.com");
        final Person test02 = new Person("test02", 20, "test02@gmail.com");
        final Person test03 = new Person("test03", 30, "test03@gmail.com");
        return List.of(test00, test01, test02, test03);
    }

    private void saveDummyUsers() {
        people.forEach(person -> {
            final List<String> columnNames = personEntityMetadata.toInsertableColumnNames();
            final List<Object> values = ReflectionUtils.getFieldValues(person, personEntityMetadata.toInsertableColumnFieldNames());
            jdbcTemplate.execute(dmlGenerator.insert(personEntityMetadata.getTableName(), columnNames, values));
        });
    }


}
