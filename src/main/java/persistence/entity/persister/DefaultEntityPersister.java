package persistence.entity.persister;

import static persistence.sql.dml.query.WhereOperator.EQUAL;

import java.lang.reflect.Field;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.entity.EntityIdExtractor;
import persistence.meta.ColumnMeta;
import persistence.meta.RelationMeta;
import persistence.meta.SchemaMeta;
import persistence.meta.store.ClassSchemaMetas;
import persistence.sql.dml.query.WhereCondition;
import persistence.sql.dml.query.builder.DeleteQueryBuilder;
import persistence.sql.dml.query.builder.InsertQueryBuilder;
import persistence.sql.dml.query.builder.UpdateQueryBuilder;

public class DefaultEntityPersister implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public DefaultEntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> Object insert(T entity) {
        insertEntity(entity);
        insertRelatedEntities(entity);
        return entity;
    }

    private <T> void insertEntity(T entity) {
        String query = InsertQueryBuilder.builder(entity).build();
        Object parentId = jdbcTemplate.insertAndGetPrimaryKey(query);
        updateEntityId(entity, parentId);
    }

    private <T> void insertRelatedEntities(T entity) {
        SchemaMeta schemaMeta = ClassSchemaMetas.get(entity.getClass());
        List<ColumnMeta> columnMetas = schemaMeta.columnMetasHasRelation();

        for (ColumnMeta columnMeta : columnMetas) {
            RelationMeta relationMeta = columnMeta.relationMeta();
            List<?> relatedEntities = extractEntities(entity, columnMeta);
            insertRelationEntities(entity, relationMeta, relatedEntities);
        }
    }

    private <T> void insertRelationEntities(T entity, RelationMeta relationMeta, List<?> relatedEntities) {
        for (Object relatedEntity : relatedEntities) {
            String query = InsertQueryBuilder.builder(
                    relatedEntity,
                    relationMeta,
                    List.of(EntityIdExtractor.extractIdValue(entity)))
                    .build();
            Object id = jdbcTemplate.insertAndGetPrimaryKey(query);
            updateEntityId(relatedEntity, id);
        }
    }


    private <T> List<?> extractEntities(T entity, ColumnMeta columnMeta) {
        try {
            Field field = entity.getClass().getDeclaredField(columnMeta.field().getName());
            field.setAccessible(true);
            return (List<?>) field.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void updateEntityId(T entity, Object id) {
        Field idField = EntityIdExtractor.extractIdField(entity);
        idField.setAccessible(true);
        try {
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void update(T entity) {
        SchemaMeta schemaMeta = new SchemaMeta(entity);
        String query = UpdateQueryBuilder.builder()
                .update(schemaMeta.tableName())
                .set(schemaMeta.columnNamesWithoutPrimaryKey(), schemaMeta.columnValuesWithoutPrimaryKey())
                .where(List.of(new WhereCondition(schemaMeta.primaryKeyColumnName(), EQUAL, EntityIdExtractor.extractIdValue(entity))))
                .build();
        jdbcTemplate.execute(query);
    }

    @Override
    public <T> void delete(T entity) {
        SchemaMeta schemaMeta = new SchemaMeta(entity);
        String query = DeleteQueryBuilder.builder()
                .delete(schemaMeta.tableName())
                .build();
        jdbcTemplate.execute(query);
    }

}
