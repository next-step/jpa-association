package persistence;

import database.DatabaseServer;
import dialect.Dialect;
import dialect.H2Dialect;
import entity.Order;
import entity.OrderItem;
import entity.Person3;
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

import java.util.List;

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

    protected static Person3 person = new Person3(1L, "test", 20, "test@test.com");
    protected static OrderItem orderItem1 = new OrderItem(1L, "A", 1);
    protected static OrderItem orderItem2 = new OrderItem(2L, "B", 10);
    protected static OrderItem orderItem3 = new OrderItem(3L, "C", 5);
    protected static Order order = new Order(1L, "test1", List.of(orderItem1, orderItem2, orderItem3));

    protected static void initForTest(EntityMetaData entityMetaData) {
        entityPersister = new EntityPersisterImpl(jdbcTemplate, entityMetaData);
        entityLoader = new EntityLoaderImpl(jdbcTemplate, entityMetaData);
        entityEntry = new SimpleEntityEntry(EntityStatus.LOADING);

        persistenceContext = new SimplePersistenceContext();
        simpleEntityManager = new SimpleEntityManager(entityPersister, entityLoader, persistenceContext, entityEntry);
        jpaRepository = new CustomJpaRepository(simpleEntityManager);
    }
}
