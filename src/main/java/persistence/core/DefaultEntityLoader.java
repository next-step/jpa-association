package persistence.core;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.entity.metadata.EntityMetadata;
import persistence.entity.metadata.RelationEntityTable;
import persistence.sql.dml.DMLQueryBuilder;

import java.util.List;

public class DefaultEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaManager entityMetaManager;
    private final DMLQueryBuilder dmlQueryBuilder;

    public DefaultEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaManager = EntityMetaManager.getInstance();
        this.dmlQueryBuilder = DMLQueryBuilder.getInstance();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        try {
            EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(clazz);

            T entity = getEntity(clazz, entityMetadata, id);
            setRelationEntity(entityMetadata, entity, id);

            return entity;
        } catch (RuntimeException e) {
            throw new RuntimeException("Entity not found", e);
        }
    }

    private void setRelationEntity(EntityMetadata entityMetadata, Object entity, Long id) {
        if (!entityMetadata.getRelationEntityTables().isEmpty()) {
            for (RelationEntityTable relation : entityMetadata.getRelationEntityTables()) {
                List<?> relationEntity = getRelationEntity(entityMetadata, relation, id);
                if (!relationEntity.isEmpty()) {
                    entityMetadata.setValue(entity, relation.getRootField(), getRelationEntity(entityMetadata, relation, id));
                }
            }
        }
    }

    private <T> T getEntity(Class<T> clazz, EntityMetadata entityMetadata, Long id) {
        String sql = getSelectDefaultQuery(entityMetadata, id);

        return jdbcTemplate.queryForObject(sql, new DefaultRowMapper<>(clazz));
    }

    private List<?> getRelationEntity(EntityMetadata entityMetadata, RelationEntityTable relation, Object id) {
        String joinSql = getSelectJoinQuery(entityMetadata, entityMetaManager.getEntityMetadata(relation.getEntityClass()), relation.getJoinColumn(), id);
        return jdbcTemplate.query(joinSql, new DefaultRowMapper<>(relation.getEntityClass()));
    }


    private String getSelectDefaultQuery(EntityMetadata entityMetadata, Object id) {
        return dmlQueryBuilder.selectByIdQuery(entityMetadata.getTableName(), entityMetadata.getColumns(), id);
    }

    private String getSelectJoinQuery(EntityMetadata mainEntity, EntityMetadata joinEntity, String joinColumn, Object id) {
        return dmlQueryBuilder.selectJoinQuery(mainEntity, joinEntity, joinColumn, id);
    }
}
