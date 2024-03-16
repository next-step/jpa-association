package persistence.entity.entitymanager;

import database.Database;
import jakarta.persistence.EntityNotFoundException;
import persistence.entity.common.EntityBinder;
import persistence.entity.common.EntityId;
import persistence.entity.common.EntityMetaCache;
import persistence.sql.dml.DMLQueryBuilder;
import persistence.sql.model.Table;

import java.util.List;

public class EntityLoader {

    private final Database database;

    public EntityLoader(Database database) {
        this.database = database;
    }

    public <T> T read(Class<T> clazz, EntityId id) {
        EntityMetaCache entityMetaCache = EntityMetaCache.INSTANCE;
        Table table = entityMetaCache.getTable(clazz);
        DMLQueryBuilder queryBuilder = new DMLQueryBuilder(table);
        String findByIdQuery = queryBuilder.buildFindByIdQuery(id);
        return database.executeQuery(clazz, findByIdQuery)
                .stream()
                .findAny()
                .orElseThrow(EntityNotFoundException::new);
    }

    public boolean isExist(Object entity) {
        EntityMetaCache entityMetaCache = EntityMetaCache.INSTANCE;
        Class<?> clazz = entity.getClass();
        Table table = entityMetaCache.getTable(clazz);
        DMLQueryBuilder queryBuilder = new DMLQueryBuilder(table);

        EntityBinder entityBinder = new EntityBinder(entity);
        EntityId id = entityBinder.getEntityId();

        String findByIdQuery = queryBuilder.buildFindByIdQuery(id);

        List<?> results = database.executeQuery(clazz, findByIdQuery);
        return !results.isEmpty();
    }
}
