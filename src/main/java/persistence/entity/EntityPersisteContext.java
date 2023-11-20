package persistence.entity;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisterFactory;

public class EntityPersisteContext {
    private final Map<Class<?>, EntityPersister> context;

    public static EntityPersisteContext create(Set<Class<?>> classSet, JdbcTemplate jdbcTemplate, Dialect dialect) {
        Map<Class<?>, EntityPersister> persiterContext = new ConcurrentHashMap<>();
        EntityPersisterFactory factory = new EntityPersisterFactory(jdbcTemplate);
        for (Class<?> clazz : classSet) {
            persiterContext.put(clazz, factory.create(clazz, dialect));
        }
        return new EntityPersisteContext(persiterContext);
    }

    private EntityPersisteContext(Map<Class<?>, EntityPersister> context) {
        this.context = context;
    }

    public EntityPersister getEntityPersister(Class<?> tClass) {
        return context.get(tClass);
    }

}
