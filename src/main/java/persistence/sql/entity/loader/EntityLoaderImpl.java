package persistence.sql.entity.loader;

import jakarta.persistence.FetchType;
import jdbc.JdbcTemplate;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.builder.EagerSelectQueryBuilder;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.collection.LazyLoadingManager;
import persistence.sql.entity.model.PrimaryDomainType;

import java.util.Collections;
import java.util.List;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityLoaderMapper entityLoaderMapper;
    private final SelectQueryBuilder selectQueryBuilder;
    private final EagerSelectQueryBuilder eagerSelectQueryBuilder;
    private final LazyLoadingManager lazyLoadingManager;

    public EntityLoaderImpl(final JdbcTemplate jdbcTemplate,
                            final EntityLoaderMapper entityLoaderMapper,
                            final SelectQueryBuilder selectQueryBuilder,
                            final EagerSelectQueryBuilder eagerSelectQueryBuilder,
                            final LazyLoadingManager lazyLoadingManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityLoaderMapper = entityLoaderMapper;
        this.selectQueryBuilder = selectQueryBuilder;
        this.eagerSelectQueryBuilder = eagerSelectQueryBuilder;
        this.lazyLoadingManager = lazyLoadingManager;
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        ColumnClause columnClause = new ColumnClause(entityMappingTable.getColumnName());
        WhereClause whereClause = new WhereClause(Criteria.emptyInstance());

        String sql = selectQueryBuilder.toSql(
                entityMappingTable.getTable(),
                columnClause,
                whereClause);

        return jdbcTemplate.query(sql, resultSet -> entityLoaderMapper.mapper(clazz, resultSet));
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        final PrimaryDomainType primaryDomainType = entityMappingTable.getPkDomainTypes();
        final ColumnClause columnClause = new ColumnClause(entityMappingTable.getColumnName());
        final Criteria criteria = Criteria.ofCriteria(Collections.singletonList(Criterion.of(primaryDomainType.getColumnName(), id.toString())));
        final WhereClause whereClause = new WhereClause(criteria);

        if (entityMappingTable.isFetchType(FetchType.EAGER)) {
            return eagerTypeSql(entityMappingTable, primaryDomainType, id, clazz);
        }

        final String sql = selectQueryBuilder.toSql(
                entityMappingTable.getTable(),
                columnClause,
                whereClause);

        T entity = jdbcTemplate.queryForObject(sql, resultSet -> entityLoaderMapper.mapper(clazz, resultSet));

        if (entityMappingTable.isFetchType(FetchType.LAZY)) {
            return lazyLoadingManager.setLazyLoading(entity, entityMappingTable);
        }

        return entity;
    }

    private <T> T eagerTypeSql(final EntityMappingTable entityMappingTable,
                               final PrimaryDomainType primaryDomainType,
                               final Object id,
                               final Class<T> clazz) {
        Criterion criterion = Criterion.of(primaryDomainType.getAlias(entityMappingTable.getTable().getAlias()), id.toString());
        Criteria criteria = Criteria.ofCriteria(Collections.singletonList(criterion));
        final WhereClause whereClause = new WhereClause(criteria);

        String sql = eagerSelectQueryBuilder.toSql(entityMappingTable, whereClause);

        return jdbcTemplate.queryForObject(sql, resultSet -> entityLoaderMapper.eagerMapper(clazz, resultSet));
    }
}
