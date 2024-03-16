package persistence.entity.database;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
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
        EntityMetadata metadata = EntityMetadataFactory.get(clazz);

        Long id = metadata.getPrimaryKeyValue(entity);
        checkGenerationStrategy(metadata.requiresIdWhenInserting(), id, metadata.getEntityClassName());
        id = metadata.requiresIdWhenInserting() ? id : null;

        Insert insert = new Insert(metadata.getTableName(),
                                   metadata.getPrimaryKey(),
                                   metadata.getGeneralColumns())
                .id(id)
                .valuesFromEntity(entity);
        return jdbcTemplate.execute(insert.toQueryString());
    }

    private void checkGenerationStrategy(boolean requiresIdWhenInserting, Long id, String entityClassName) {
        if (requiresIdWhenInserting && id == null) {
            throw new PrimaryKeyMissingException(entityClassName);
        }
    }

    public void update(Class<?> clazz, Long id, Map<String, Object> changes) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        String query = new Update(entityMetadata.getTableName(),
                                  entityMetadata.getGeneralColumns(),
                                  entityMetadata.getPrimaryKey())
                .changes(changes)
                .byId(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }

    public void update(Class<?> clazz, Long id, Object entity) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        String query = new Update(entityMetadata.getTableName(),
                                  entityMetadata.getGeneralColumns(),
                                  entityMetadata.getPrimaryKey())
                .changes(entity)
                .byId(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }

    public void delete(Class<?> clazz, Long id) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        String query = new Delete(entityMetadata.getTableName(),
                                  entityMetadata.getAllEntityColumns(),
                                  entityMetadata.getPrimaryKey()
        )
                .id(id)
                .buildQuery();
        jdbcTemplate.execute(query);
    }
}
