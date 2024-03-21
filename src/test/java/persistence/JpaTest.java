package persistence;

import database.DatabaseServer;
import dialect.Dialect;
import dialect.H2Dialect;
import jdbc.JdbcTemplate;
import persistence.context.PersistenceContext;
import persistence.context.SimplePersistenceContext;
import persistence.entity.CustomJpaRepository;
import persistence.entity.EntityLoader;
import persistence.entity.EntityLoaderImpl;
import persistence.entity.EntityPersister;
import persistence.entity.EntityPersisterImpl;
import persistence.entity.JpaRepository;
import persistence.entity.SimpleEntityEntry;
import persistence.entity.SimpleEntityManager;
import pojo.EntityMetaData;
import pojo.EntityStatus;

public abstract class JpaTest {

    protected static Dialect dialect = new H2Dialect();
    protected static DatabaseServer server;
    protected static JdbcTemplate jdbcTemplate;
    protected static EntityPersister entityPersister;
    protected static EntityLoader entityLoader;
    protected static SimpleEntityManager simpleEntityManager;
    protected static PersistenceContext persistenceContext;
    protected static SimpleEntityEntry entityEntry;
    protected static JpaRepository jpaRepository;

    protected static void initForTest(EntityMetaData entityMetaData) {
        entityPersister = new EntityPersisterImpl(jdbcTemplate, entityMetaData);
        entityLoader = new EntityLoaderImpl(jdbcTemplate, entityMetaData);
        entityEntry = new SimpleEntityEntry(EntityStatus.LOADING);

        persistenceContext = new SimplePersistenceContext();
        simpleEntityManager = new SimpleEntityManager(entityPersister, entityLoader, persistenceContext, entityEntry);
        jpaRepository = new CustomJpaRepository(simpleEntityManager);
    }
}
