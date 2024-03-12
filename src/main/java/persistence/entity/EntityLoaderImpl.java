package persistence.entity;

import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.sql.column.JoinTableColumn;
import persistence.sql.column.TableColumn;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.mapper.GenericRowMapper;

import java.util.List;
import java.util.stream.Collectors;

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
        List<JoinTableColumn> joinTableColumns = tableColumn.getJoinTableColumn();
        if (joinTableColumns.isEmpty()) {
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
        List<JoinTableColumn> joinTableColumns = tableColumn.getJoinTableColumn();
        if (joinTableColumns.isEmpty()) {
            String query = queryBuilder.selectFromWhereIdClause(id);
            return jdbcTemplate.queryForObject(query, new GenericRowMapper<>(clazz));
        }
        T instance = null;

        boolean isEager = joinTableColumns.stream().anyMatch(joinTable -> !joinTable.getAssociationEntity().isLazy());
        if (isEager) {
            List<JoinTableColumn> eagerJoinTables = joinTableColumns.stream()
                    .filter(joinTable -> !joinTable.getAssociationEntity().isLazy())
                    .collect(Collectors.toList());
            String query = queryBuilder.selectFromJoinWhereIdClause(eagerJoinTables, id);
            instance = jdbcTemplate.queryForObject(query, new GenericRowMapper<>(clazz));
        }else{
            String aa = queryBuilder.selectFromWhereIdClause(id);
            instance = jdbcTemplate.queryForObject(aa, new GenericRowMapper<>(clazz));
        }

        List<JoinTableColumn> lazyJoinTables = joinTableColumns.stream()
                .filter(joinTable -> joinTable.getAssociationEntity().isLazy())
                .collect(Collectors.toList());
        setProxy(lazyJoinTables, instance);
        return instance;
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
