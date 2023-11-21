package persistence.entity.impl.retrieve;

import java.sql.Connection;
import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.impl.EntityRowMapper;
import persistence.entity.proxy.CollectionProxyWrapper;
import persistence.sql.dialect.ColumnType;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.WherePredicate;
import persistence.sql.dml.statement.SelectStatementBuilder;
import persistence.sql.schema.meta.ColumnMeta;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.EntityObjectMappingMeta;

public class EntityCollectionLoader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JdbcTemplate jdbcTemplate;
    private final CollectionObjectMapper collectionObjectMapper;
    private final CollectionProxyWrapper collectionProxyWrapper;

    public EntityCollectionLoader(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.collectionObjectMapper = new CollectionObjectMapper();
        this.collectionProxyWrapper = new CollectionProxyWrapper();
    }

    public <T> T loadCollection(Class<T> clazz, Object instance, ColumnType columnType) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(instance, columnType);
        final EntityClassMappingMeta classMappingMeta = objectMappingMeta.getEntityClassMappingMeta();
        final List<ColumnMeta> relationColumnMetaList = classMappingMeta.getRelationColumnMetaList();

        for (ColumnMeta columnMeta : relationColumnMetaList) {
            if (columnMeta.isEagerLoading()) {
                eagerLoad(instance, columnMeta, objectMappingMeta, columnType);
                continue;
            }

            lazyLoad(instance, columnMeta, objectMappingMeta, columnType);
        }

        return clazz.cast(instance);
    }

    private void eagerLoad(Object parent, ColumnMeta columnMeta, EntityObjectMappingMeta objectMappingMeta, ColumnType columnType) {
        final List<?> loadedChildList = selectRelation(columnMeta, objectMappingMeta, columnType);

        collectionObjectMapper.mappingFieldRelation(parent, columnMeta, loadedChildList);
    }

    private void lazyLoad(Object instance, ColumnMeta columnMeta, EntityObjectMappingMeta objectMappingMeta, ColumnType columnType) {
        final Object collectionProxy = collectionProxyWrapper.wrap(
            columnMeta.getColumnType(),
            () -> selectRelation(columnMeta, objectMappingMeta, columnType)
        );

        collectionObjectMapper.mappingFieldRelation(instance, columnMeta, collectionProxy);
    }

    private List<?> selectRelation(ColumnMeta columnMeta, EntityObjectMappingMeta objectMappingMeta, ColumnType columnType) {
        final String selectRelationSql = SelectStatementBuilder.builder()
            .selectFrom(columnMeta.getJoinColumnTableType(), columnType)
            .where(WherePredicate.of(columnMeta.getJoinColumnName(), objectMappingMeta.getIdValue(), new EqualOperator()))
            .build();

        logger.info(selectRelationSql);

        return jdbcTemplate.query(
            selectRelationSql,
            new EntityRowMapper<>(columnMeta.getJoinColumnTableType(), columnType)
        );
    }
}
