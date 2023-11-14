package persistence.entity.impl.retrieve;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.entity.impl.EntityRowMapper;
import persistence.sql.dialect.ColumnType;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.WherePredicate;
import persistence.sql.dml.statement.SelectStatementBuilder;
import persistence.sql.schema.meta.ColumnMeta;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.EntityObjectMappingMeta;

public class EntityCollectionLoader {

    private final JdbcTemplate jdbcTemplate;

    public EntityCollectionLoader(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
    }

    public <T> T loadCollection(Class<T> clazz, Object instance, ColumnType columnType) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(instance, columnType);
        final EntityClassMappingMeta classMappingMeta = objectMappingMeta.getEntityClassMappingMeta();
        final List<ColumnMeta> relationColumnMetaList = classMappingMeta.getRelationColumnMetaList();

        for (ColumnMeta columnMeta : relationColumnMetaList) {
            if (!columnMeta.isLazyLoading()) {
                eagerLoad(instance, columnMeta, objectMappingMeta, columnType);
            }

            // TODO: lazy load proxy
        }

        return clazz.cast(instance);
    }

    private void eagerLoad(Object instance, ColumnMeta columnMeta, EntityObjectMappingMeta objectMappingMeta, ColumnType columnType) {
        final String selectRelationSql = SelectStatementBuilder.builder()
            .selectFrom(columnMeta.getJoinColumnTableType(), columnType)
            .where(WherePredicate.of(columnMeta.getJoinColumnName(), objectMappingMeta.getIdValue(), new EqualOperator()))
            .build();

        final List<?> loadedRelation = jdbcTemplate.query(selectRelationSql,
            new EntityRowMapper<>(columnMeta.getJoinColumnTableType(), columnType));

        mappingFieldRelation(instance, columnMeta, loadedRelation);
    }

    private static void mappingFieldRelation(Object instance, ColumnMeta columnMeta, List<?> loadedRelation) {
        try {
            final Field field = instance.getClass().getDeclaredField(columnMeta.getFieldName());
            field.setAccessible(true);
            field.set(instance, loadedRelation);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
