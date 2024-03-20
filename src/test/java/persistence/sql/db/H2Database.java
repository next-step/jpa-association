package persistence.sql.db;

import database.DatabaseServer;
import database.H2;
import domain.LazyOrder;
import domain.Order;
import domain.OrderItem;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import persistence.sql.ddl.query.builder.CreateQueryBuilder;
import persistence.sql.ddl.query.builder.DropQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectResolutionInfo;
import persistence.sql.dialect.database.Database;
import persistence.sql.dml.query.builder.*;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.collection.*;
import persistence.sql.entity.loader.EntityLoader;
import persistence.sql.entity.loader.EntityLoaderImpl;
import persistence.sql.entity.loader.EntityLoaderMapper;
import persistence.sql.entity.manager.EntityManager;
import persistence.sql.entity.manager.EntityManagerImpl;
import persistence.sql.entity.persister.EntityPersister;
import persistence.sql.entity.persister.EntityPersisterImpl;

import java.sql.SQLException;

public abstract class H2Database {

    protected static EntityMappingTable entityMappingTable;
    protected static EntityMappingTable eagerEntityMappingTable;
    protected static EntityMappingTable lazyEntityMappingTable;

    protected static DatabaseServer server;

    protected static JdbcTemplate jdbcTemplate;

    protected static SelectQueryBuilder selectQueryBuilder;
    protected static EagerSelectQueryBuilder eagerSelectQueryBuilder;
    protected static InsertQueryBuilder insertQueryBuilder;
    protected static UpdateQueryBuilder updateQueryBuilder;
    protected static DeleteQueryBuilder deleteQueryBuilder;


    protected static EntityLoaderMapper entityLoaderMapper;
    protected static EntityLoader entityLoader;
    protected static EntityPersister entityPersister;
    protected static CollectionPersister collectionPersister;
    protected static CollectionLoader collectionLoader;
    protected static LazyLoadingManager lazyLoadingManager;

    protected static EntityManager entityManager;

    @BeforeAll
    static void setUpAll() throws SQLException {
        server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        collectionPersister = new CollectionPersisterImpl();

        selectQueryBuilder = SelectQueryBuilder.getInstance();
        eagerSelectQueryBuilder = EagerSelectQueryBuilder.getInstance();
        insertQueryBuilder = InsertQueryBuilder.getInstance();
        updateQueryBuilder = UpdateQueryBuilder.getInstance();
        deleteQueryBuilder = DeleteQueryBuilder.getInstance();
        entityLoaderMapper = EntityLoaderMapper.getInstance();

        entityPersister = new EntityPersisterImpl(
                jdbcTemplate,
                insertQueryBuilder,
                updateQueryBuilder,
                deleteQueryBuilder);
        collectionLoader = new CollectionLoaderImpl(
                entityLoaderMapper,
                selectQueryBuilder,
                jdbcTemplate
        );
        lazyLoadingManager = new LazyLoadingManager(collectionPersister, collectionLoader);
        entityLoader = new EntityLoaderImpl(
                jdbcTemplate,
                entityLoaderMapper,
                selectQueryBuilder,
                eagerSelectQueryBuilder,
                lazyLoadingManager);

        entityManager = new EntityManagerImpl(entityLoader, entityPersister);

        createTable();
    }

    private static void createTable() throws SQLException {
        DialectResolutionInfo dialectResolutionInfo = new DialectResolutionInfo(server.getConnection().getMetaData());
        Dialect dialect = Database.from(dialectResolutionInfo).getDialectSupplier().get();
        entityMappingTable = EntityMappingTable.from(Person.class);
        CreateQueryBuilder createQueryBuilder = CreateQueryBuilder.of(entityMappingTable, dialect.getTypeMapper(), dialect.getConstantTypeMapper());
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(entityMappingTable);

        eagerEntityMappingTable = EntityMappingTable.from(Order.class);
        CreateQueryBuilder eagerCreateBuilder = CreateQueryBuilder.of(eagerEntityMappingTable, dialect.getTypeMapper(), dialect.getConstantTypeMapper());
        DropQueryBuilder eagerDropBuilder = new DropQueryBuilder(eagerEntityMappingTable);

        lazyEntityMappingTable = EntityMappingTable.from(LazyOrder.class);
        CreateQueryBuilder lazyCreateBuilder = CreateQueryBuilder.of(lazyEntityMappingTable, dialect.getTypeMapper(), dialect.getConstantTypeMapper());
        DropQueryBuilder lazyDropBuilder = new DropQueryBuilder(lazyEntityMappingTable);

        EntityMappingTable orderItemEntityMappingTable = EntityMappingTable.from(OrderItem.class);
        CreateQueryBuilder orderItemCreateBuilder = CreateQueryBuilder.of(orderItemEntityMappingTable, dialect.getTypeMapper(), dialect.getConstantTypeMapper());
        DropQueryBuilder orderItemDropBuilder = new DropQueryBuilder(orderItemEntityMappingTable);

        jdbcTemplate.execute(dropQueryBuilder.toSql());
        jdbcTemplate.execute(eagerDropBuilder.toSql());
        jdbcTemplate.execute(lazyDropBuilder.toSql());
        jdbcTemplate.execute(orderItemDropBuilder.toSql());

        jdbcTemplate.execute(createQueryBuilder.toSql());
        jdbcTemplate.execute(orderItemCreateBuilder.toSql());
        jdbcTemplate.execute(eagerCreateBuilder.toSql());
        jdbcTemplate.execute(lazyCreateBuilder.toSql());
    }
}
