package persistence.entity;

import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.sql.column.*;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.mapper.GenericRowMapper;

import java.util.List;

public class EntityLoaderImpl implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;


    public EntityLoaderImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = new SelectQueryBuilder();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        SelectQueryBuilder queryBuilder = selectQueryBuilder.build(clazz);
        TableColumn tableColumn = new TableColumn(clazz);
        JoinTableColumns joinTableColumns = tableColumn.getJoinTableColumns();
        if (joinTableColumns.hasNotAssociation()) {
            String query = queryBuilder.selectFromWhereIdClause(id);
            return jdbcTemplate.queryForObject(query, new GenericRowMapper<>(clazz));
        }
        return getInstance(clazz, id, joinTableColumns);
    }
    private <T> T getInstance(Class<T> clazz, Long id, JoinTableColumns joinTableColumns) {
        SelectQueryBuilder queryBuilder = selectQueryBuilder.build(clazz);
        if (joinTableColumns.hasEager()) {
            JoinTableColumns eagerJoinTables = joinTableColumns.getEagerJoinTables();
            String query = queryBuilder.selectFromJoinWhereIdClause(eagerJoinTables, id);
            return jdbcTemplate.queryForObject(query, new GenericRowMapper<>(clazz));
        }

        String fromQuery = queryBuilder.selectFromWhereIdClause(id);
        T instance = jdbcTemplate.queryForObject(fromQuery, new GenericRowMapper<>(clazz));

        if (joinTableColumns.hasLazy()) {
            List<JoinTableColumn> lazyJoinTables = joinTableColumns.getLazyJoinTables();
            setProxy(lazyJoinTables, instance, id);
        }
        return instance;
    }

    private <T> void setProxy(List<JoinTableColumn> lazyJoinTables, T rootEntity, Object id) {
        for (JoinTableColumn lazyJoinTable : lazyJoinTables) {
            Enhancer enhancer = createEnhancer(lazyJoinTable, id);
            Object proxy = enhancer.create();
            lazyJoinTable.getAssociationEntity().setAssociationColumn(rootEntity, proxy);
        }
    }

    private Enhancer createEnhancer(JoinTableColumn lazyJoinTable, Object id) {
        Column joinEntityColumn = lazyJoinTable.getAssociationEntity().getJoinEntityColumn();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(List.class);
        enhancer.setCallback((LazyLoader) () -> findAllAssociation(lazyJoinTable.getClazz(), joinEntityColumn, id));
        return enhancer;
    }

    private <T> List<T> findAllAssociation(Class<T> clazz, Column joinEntityColumn, Object id) {
        TableColumn tableColumn = new TableColumn(clazz);
        JoinTableColumns joinTableColumns = tableColumn.getJoinTableColumns();
        SelectQueryBuilder queryBuilder = selectQueryBuilder.build(clazz);
        if (joinTableColumns.hasNotAssociation()) {
            String query = queryBuilder.selectFromWhereIdClause(joinEntityColumn, tableColumn.getName(), id);
            return jdbcTemplate.query(query, new GenericRowMapper<>(clazz));
        }
        String query = queryBuilder.selectFromJoinWhereIdClause(joinTableColumns, id);
        return jdbcTemplate.query(query, new GenericRowMapper<>(clazz));
    }
}
