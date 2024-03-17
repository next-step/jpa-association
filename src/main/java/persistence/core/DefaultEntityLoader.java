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
            String sql = getSelectDefaultQuery(entityMetadata, id);

            T object = jdbcTemplate.queryForObject(sql, new DefaultRowMapper<>(clazz));

            if (!entityMetadata.getRelationEntityTables().isEmpty()) {
                for (RelationEntityTable relation : entityMetadata.getRelationEntityTables()) {
                    String joinSql = getSelectJoinQuery(entityMetadata, entityMetaManager.getEntityMetadata(relation.getEntity()), relation.getJoinColumnName(), id);
                    List<?> data = jdbcTemplate.query(joinSql, new DefaultRowMapper<>(relation.getEntity()));

                    entityMetadata.setValue(object, relation.getRootField(), data);
                }
            }

            return object;
        } catch (RuntimeException e) {
            throw new RuntimeException("Entity not found", e);
        }
    }

    private String getSelectDefaultQuery(EntityMetadata entityMetadata, Object id) {
        return dmlQueryBuilder.selectByIdQuery(entityMetadata.getTableName(), entityMetadata.getColumns(), id);
    }

    private String getSelectJoinQuery(EntityMetadata mainEntity, EntityMetadata joinEntity, String joinColumn, Object id) {
        return dmlQueryBuilder.selectJoinQuery(mainEntity, joinEntity, joinColumn, id);
    }
}
