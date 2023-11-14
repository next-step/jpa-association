package persistence.entity.impl.retrieve;

import java.sql.Connection;
import jdbc.JdbcTemplate;
import persistence.entity.impl.EntityRowMapper;
import persistence.sql.dialect.ColumnType;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.WherePredicate;
import persistence.sql.dml.statement.SelectStatementBuilder;
import persistence.sql.schema.meta.EntityClassMappingMeta;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityCollectionLoader entityCollectionLoader;

    public EntityLoaderImpl(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.entityCollectionLoader = new EntityCollectionLoader(connection);
    }

    @Override
    public <T> T load(Class<T> clazz, Object id, ColumnType columnType) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(clazz, columnType);

        final SelectStatementBuilder selectStatementBuilder = SelectStatementBuilder.builder()
            .selectFrom(clazz, columnType);

        final String selectSql = selectStatementBuilder
            .where(WherePredicate.of(classMappingMeta.getIdFieldColumnName(), id, new EqualOperator()))
            .build();

        final T queryObject = jdbcTemplate.queryForObject(selectSql, new EntityRowMapper<>(clazz, columnType));

        if (classMappingMeta.hasNoRelation()) {
            return queryObject;
        }

        return entityCollectionLoader.loadCollection(clazz, queryObject, columnType);
    }
}
