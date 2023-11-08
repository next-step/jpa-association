package persistence.entity.manager;

import jdbc.JdbcTemplate;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;
import persistence.core.PersistenceEnvironment;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;
import persistence.entity.proxy.EntityProxyFactory;
import persistence.sql.dml.DmlGenerator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleEntityManagerFactory implements EntityManagerFactory {
    private final EntityMetadataProvider entityMetadataProvider;
    private final EntityPersisters entityPersisters;
    private final EntityLoaders entityLoaders;
    private final EntityProxyFactory entityProxyFactory;


    public SimpleEntityManagerFactory(final EntityMetadataProvider entityMetadataProvider, final EntityScanner entityScanner, final PersistenceEnvironment persistenceEnvironment) {
        this.entityMetadataProvider = entityMetadataProvider;

        final List<Class<?>> entityClasses = entityScanner.getEntityClasses();
        final DmlGenerator dmlGenerator = persistenceEnvironment.getDmlGenerator();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(persistenceEnvironment.getConnection());

        final Map<Class<?>, EntityPersister> persisters = createEntityPersisters(entityClasses, dmlGenerator, jdbcTemplate);
        final Map<Class<?>, EntityLoader<?>> loaders = createEntityLoaders(entityClasses, dmlGenerator, jdbcTemplate);

        this.entityPersisters = new EntityPersisters(persisters);
        this.entityLoaders = new EntityLoaders(loaders);
        this.entityProxyFactory = new EntityProxyFactory(entityLoaders);
    }

    private Map<Class<?>, EntityPersister> createEntityPersisters(final List<Class<?>> entityClasses, final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        return entityClasses.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        clazz -> new EntityPersister(clazz, dmlGenerator, jdbcTemplate)
                ));
    }

    private Map<Class<?>, EntityLoader<?>> createEntityLoaders(final List<Class<?>> entityClasses, final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        return entityClasses.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        clazz -> EntityLoader.of(entityMetadataProvider.getEntityMetadata(clazz), dmlGenerator, jdbcTemplate)
                ));
    }

    @Override
    public EntityManager createEntityManager() {
        return new SimpleEntityManager(entityMetadataProvider, entityPersisters, entityLoaders, entityProxyFactory);
    }
}

