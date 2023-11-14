package persistence.entity.impl.retrieve;

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
    private final CollectionObjectMapper collectionObjectMapper;

    public EntityCollectionLoader(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.collectionObjectMapper = new CollectionObjectMapper();
    }

    public <T> T loadCollection(Class<T> clazz, Object instance, ColumnType columnType) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(instance, columnType);
        final EntityClassMappingMeta classMappingMeta = objectMappingMeta.getEntityClassMappingMeta();
        final List<ColumnMeta> relationColumnMetaList = classMappingMeta.getRelationColumnMetaList();

        for (ColumnMeta columnMeta : relationColumnMetaList) {
            if (columnMeta.isEagerLoading()) {
                eagerLoad(instance, columnMeta, objectMappingMeta, columnType);
            }

            // TODO: lazy load proxy
        }

        return clazz.cast(instance);
    }

    private void eagerLoad(Object parent, ColumnMeta columnMeta, EntityObjectMappingMeta objectMappingMeta, ColumnType columnType) {
        final String selectRelationSql = SelectStatementBuilder.builder()
            .selectFrom(columnMeta.getJoinColumnTableType(), columnType)
            .where(WherePredicate.of(columnMeta.getJoinColumnName(), objectMappingMeta.getIdValue(), new EqualOperator()))
            .build();

        final List<?> loadedChildList = jdbcTemplate.query(selectRelationSql,
            new EntityRowMapper<>(columnMeta.getJoinColumnTableType(), columnType));

        collectionObjectMapper.mappingFieldRelation(parent, columnMeta, loadedChildList);
    }
}
