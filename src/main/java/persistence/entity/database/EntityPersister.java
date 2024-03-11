package persistence.entity.database;

import database.mapping.ColumnValueMap;
import database.mapping.EntityClass;
import database.mapping.EntityMetadata;
import database.sql.dml.Delete;
import database.sql.dml.Insert;
import database.sql.dml.Update;
import jdbc.JdbcTemplate;

import java.util.Map;

/**
 * 엔터티의 메타데이터와 데이터베이스 매핑 정보를 제공하고,
 * 변경된 엔터티를 데이터베이스에 동기화하는 역할
 */
public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long insert(Class<?> clazz, Object entity) {
        EntityClass entityClass = EntityClass.of(clazz);
        EntityMetadata metadata = entityClass.getMetadata();

        Long id = metadata.getPrimaryKeyValue(entity);
        checkGenerationStrategy(metadata, id);
        id = metadata.requiresIdWhenInserting() ? id : null;
        Insert insert = new Insert(metadata)
                .id(id)
                .values(columnValues(entity));
        return jdbcTemplate.execute(insert.toQueryString());
    }

    private void checkGenerationStrategy(EntityMetadata entityMetadata, Long id) {
        if (entityMetadata.requiresIdWhenInserting() && id == null) {
            throw new PrimaryKeyMissingException(entityMetadata.getEntityClassName());
        }
    }

    public void update(Class<?> clazz, Long id, Map<String, Object> changes) {
        doUpdate(clazz, id, changes);
    }

    public void update(Class<?> clazz, Long id, Object entity) {
        update(clazz, id, columnValues(entity));
    }

    private void doUpdate(Class<?> clazz, Long id, Map<String, Object> map) {
        EntityClass entityClass = EntityClass.of(clazz);
        EntityMetadata metadata = entityClass.getMetadata();

        Update update = new Update(metadata);
        String query = update.buildQuery(id, map);
        jdbcTemplate.execute(query);
    }

    public void delete(Class<?> clazz, Long id) {
        EntityClass entityClass = EntityClass.of(clazz);
        EntityMetadata metadata = entityClass.getMetadata();

        Delete delete = new Delete(metadata);
        String query = delete.buildQuery(Map.of("id", id));
        jdbcTemplate.execute(query);
    }

    private Map<String, Object> columnValues(Object entity) {
        return ColumnValueMap.valueMapFromEntity(entity);
    }
}
