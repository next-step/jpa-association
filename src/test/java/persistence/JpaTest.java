package persistence;

import database.DatabaseServer;
import dialect.Dialect;
import dialect.H2Dialect;
import jdbc.JdbcTemplate;
import persistence.context.PersistenceContext;
import persistence.context.SimplePersistenceContext;
import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;
import persistence.entity.JpaRepository;
import persistence.entity.SimpleEntityEntry;
import persistence.entity.SimpleEntityManager;
import pojo.EntityMetaData;

public abstract class JpaTest {

    protected static Dialect dialect = new H2Dialect();
    protected static EntityMetaData entityMetaData;
    protected static DatabaseServer server;
    protected static JdbcTemplate jdbcTemplate;
    protected static EntityPersister entityPersister;
    protected static EntityLoader entityLoader;
    protected static SimpleEntityManager simpleEntityManager;
    protected static PersistenceContext persistenceContext = new SimplePersistenceContext();
    protected static SimpleEntityEntry entityEntry;
    protected static JpaRepository jpaRepository;
}
