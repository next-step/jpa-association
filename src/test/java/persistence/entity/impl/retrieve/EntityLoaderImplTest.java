package persistence.entity.impl.retrieve;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.impl.EntityManagerFactoryImpl;
import persistence.sql.ddl.generator.CreateDDLQueryGenerator;
import persistence.sql.ddl.generator.DropDDLQueryGenerator;
import persistence.sql.dialect.ColumnType;
import persistence.sql.dialect.H2ColumnType;
import persistence.sql.dml.Database;
import persistence.sql.dml.JdbcTemplate;

@DisplayName("EntityLoader 테스트")
class EntityLoaderImplTest {

    private DatabaseServer server;

    private Database jdbcTemplate;

    private EntityManager entityManager;

    private ColumnType columnType;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        Connection connection = server.getConnection();

        columnType = new H2ColumnType();
        final EntityManagerFactoryImpl emf = new EntityManagerFactoryImpl(connection, columnType);
        entityManager = emf.createEntityManager();
        jdbcTemplate = new JdbcTemplate(connection);
        CreateDDLQueryGenerator createDDLQueryGenerator = new CreateDDLQueryGenerator(columnType);
        jdbcTemplate.execute(createDDLQueryGenerator.create(EntityLoaderEntity.class));
    }

    @AfterEach
    void tearDown() throws Exception {
        DropDDLQueryGenerator dropDDLQueryGenerator = new DropDDLQueryGenerator(new H2ColumnType());
        jdbcTemplate.execute(dropDDLQueryGenerator.drop(EntityLoaderEntity.class));
        entityManager.close();
        server.stop();
    }

    @Test
    @DisplayName("EntityLoader를 통해 Entity를 불러올 수 있다.")
    void entityLoaderCanLoad() throws SQLException {
        final EntityLoaderEntity entity = new EntityLoaderEntity();
        final EntityLoaderEntity loadedEntity = (EntityLoaderEntity) entityManager.persist(entity);

        final EntityLoaderImpl entityLoader = new EntityLoaderImpl(server.getConnection());

        final EntityLoaderEntity load = entityLoader.load(EntityLoaderEntity.class, loadedEntity.getId(), columnType);
        assertAll(
            () -> assertThat(load.getId()).isEqualTo(loadedEntity.getId())
        );
    }

    @Entity
    static class EntityLoaderEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        public EntityLoaderEntity(Long id) {
            this.id = id;
        }

        protected EntityLoaderEntity() {
        }

        public Long getId() {
            return id;
        }
    }
}
