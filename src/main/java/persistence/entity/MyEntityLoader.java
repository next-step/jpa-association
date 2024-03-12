package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import jdbc.RowMapperFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.sql.dml.SelectAllQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.meta.AssociationTable;
import persistence.sql.meta.Table;

import java.util.List;

public class MyEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;

    public MyEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = SelectQueryBuilder.getInstance();
    }

    @Override
    public <T> T find(Class<T> clazz, Object Id) {
        Table table = Table.from(clazz);
        if (!table.containsAssociation()) {
            String query = selectQueryBuilder.build(table, Id);
            RowMapper<T> rowMapper = RowMapperFactory.create(clazz);
            return jdbcTemplate.queryForObject(query, rowMapper);
        }
        String query = selectQueryBuilder.buildWithJoin(table, Id);
        RowMapper<T> rowMapper = RowMapperFactory.create(clazz);
        T object = jdbcTemplate.queryForObject(query, rowMapper);
        if (table.containsLazyAssociation()) {
            List<AssociationTable> associationTables = table.getLazyAssociationTables();
            for (AssociationTable associationTable : associationTables) {
                setProxy(associationTable, object);
            }
        }
        return object;
    }

    private <T> void setProxy(AssociationTable associationTable, T object) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(List.class);
        enhancer.setCallback((LazyLoader) () -> findAll(associationTable.getClazz()));
        Object proxy = enhancer.create();
        associationTable.getField().setAccessible(true);
        try {
            associationTable.getField().set(object, proxy);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private List<?> findAll(Class<?> clazz) {
        SelectAllQueryBuilder selectAllQueryBuilder = new SelectAllQueryBuilder();
        RowMapper<?> rowMapper = RowMapperFactory.create(clazz);
        return jdbcTemplate.query(selectAllQueryBuilder.build(clazz), rowMapper);
    }
}
