package persistence.sql.entity.loader;

import jdbc.JdbcTemplate;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.builder.EagerSelectQueryBuilder;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.PrimaryDomainType;

import java.util.Collections;
import java.util.List;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityLoaderMapper entityLoaderMapper;
    private final SelectQueryBuilder selectQueryBuilder;
    private final EagerSelectQueryBuilder eagerSelectQueryBuilder;

    public EntityLoaderImpl(final JdbcTemplate jdbcTemplate,
                            final EntityLoaderMapper entityLoaderMapper,
                            final SelectQueryBuilder selectQueryBuilder,
                            final EagerSelectQueryBuilder eagerSelectQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityLoaderMapper = entityLoaderMapper;
        this.selectQueryBuilder = selectQueryBuilder;
        this.eagerSelectQueryBuilder = eagerSelectQueryBuilder;
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());
        WhereClause whereClause = new WhereClause(Criteria.emptyInstance());

        String sql = selectQueryBuilder.toSql(
                entityMappingTable.getTableName(),
                columnClause,
                whereClause);

        return jdbcTemplate.query(sql, resultSet -> entityLoaderMapper.mapper(clazz, resultSet));
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        final PrimaryDomainType primaryDomainType = entityMappingTable.getPkDomainTypes();
        final ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());
        final Criteria criteria = Criteria.ofCriteria(Collections.singletonList(Criterion.of(primaryDomainType.getColumnName(), id.toString())));
        final WhereClause whereClause = new WhereClause(criteria);

        if(entityMappingTable.isEagerType()) {
            return eagerTypeSql(entityMappingTable, primaryDomainType, id, clazz);
        }

        final String sql = selectQueryBuilder.toSql(
                entityMappingTable.getTableName(),
                columnClause,
                whereClause);

        return jdbcTemplate.queryForObject(sql, resultSet -> entityLoaderMapper.mapper(clazz, resultSet));
    }

    private <T> T eagerTypeSql(final EntityMappingTable entityMappingTable,
                               final PrimaryDomainType primaryDomainType,
                               final Object id,
                               final Class<T> clazz) {
        Criterion criterion = Criterion.of(primaryDomainType.getAlias(entityMappingTable.getTableName().getAlias()), id.toString());
        Criteria criteria = Criteria.ofCriteria(Collections.singletonList(criterion));
        final WhereClause whereClause = new WhereClause(criteria);

        String sql = eagerSelectQueryBuilder.toSql(entityMappingTable, whereClause);

        return jdbcTemplate.queryForObject(sql, resultSet -> entityLoaderMapper.eagerMapper(clazz, resultSet));
    }
}
