package persistence.sql.entity.collection;

import jdbc.JdbcTemplate;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.loader.EntityLoaderMapper;
import persistence.sql.entity.model.PrimaryDomainType;

import java.util.Collections;
import java.util.List;

public class CollectionLoaderImpl implements CollectionLoader {
    private final EntityLoaderMapper entityLoaderMapper;
    private final SelectQueryBuilder selectQueryBuilder;
    private final JdbcTemplate jdbcTemplate;

    public CollectionLoaderImpl(final EntityLoaderMapper entityLoaderMapper,
                                final SelectQueryBuilder selectQueryBuilder,
                                final JdbcTemplate jdbcTemplate) {
        this.entityLoaderMapper = entityLoaderMapper;
        this.selectQueryBuilder = selectQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> List<T> findById(final Class<T> clazz, final Object id) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        final PrimaryDomainType primaryDomainType = entityMappingTable.getPkDomainTypes();
        final ColumnClause columnClause = new ColumnClause(entityMappingTable.getColumnName());
        final Criteria criteria = Criteria.ofCriteria(Collections.singletonList(Criterion.of(primaryDomainType.getColumnName(), id.toString())));
        final WhereClause whereClause = new WhereClause(criteria);

        final String sql = selectQueryBuilder.toSql(
                entityMappingTable.getTable(),
                columnClause,
                whereClause);

        return jdbcTemplate.query(sql , resultSet -> entityLoaderMapper.mapper(clazz, resultSet));
    }


}
