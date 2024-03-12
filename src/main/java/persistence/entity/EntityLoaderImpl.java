package persistence.entity;

import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.sql.column.JoinTableColumn;
import persistence.sql.column.JoinTableColumns;
import persistence.sql.column.TableColumn;
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

    public <T> List<T> findAll(Class<T> clazz) {
        SelectQueryBuilder queryBuilder = selectQueryBuilder.build(clazz);
        TableColumn tableColumn = new TableColumn(clazz);
        JoinTableColumns joinTableColumns = tableColumn.getJoinTableColumns();
        if (joinTableColumns.hasNotAssociation()) {
            String query = queryBuilder.selectFromClause();
            return jdbcTemplate.query(query, new GenericRowMapper<>(clazz));
        }
        String query = queryBuilder.selectFromJoinClause(joinTableColumns);
        return jdbcTemplate.query(query, new GenericRowMapper<>(clazz));
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
        T instance = getInstance(clazz, id, joinTableColumns);

        List<JoinTableColumn> lazyJoinTables = joinTableColumns.getLazyJoinTables();
        setProxy(lazyJoinTables, instance);
        return instance;
    }

    private <T> T getInstance(Class<T> clazz, Long id, JoinTableColumns joinTableColumns) {
        SelectQueryBuilder queryBuilder = selectQueryBuilder.build(clazz);
        if (joinTableColumns.hasEager()) {
            JoinTableColumns eagerJoinTables = joinTableColumns.getEagerJoinTables();
            String query = queryBuilder.selectFromJoinWhereIdClause(eagerJoinTables, id);
            return jdbcTemplate.queryForObject(query, new GenericRowMapper<>(clazz));
        }

        String fromQuery = queryBuilder.selectFromWhereIdClause(id);
        return jdbcTemplate.queryForObject(fromQuery, new GenericRowMapper<>(clazz));
    }

    private <T> void setProxy(List<JoinTableColumn> lazyJoinTables, T rootEntity) {
        for (JoinTableColumn lazyJoinTable : lazyJoinTables) {
            Enhancer enhancer = createEnhancer(lazyJoinTable.getClazz());
            Object proxy = enhancer.create();
            lazyJoinTable.getAssociationEntity().setAssociationColumn(rootEntity, proxy);
        }
    }

    private <T> Enhancer createEnhancer(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(List.class);
        enhancer.setCallback((LazyLoader) () -> findAll(clazz));
        return enhancer;
    }
}
