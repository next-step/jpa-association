package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import jdbc.RowMapperFactory;
import persistence.proxy.MyProxyFactory;
import persistence.proxy.ProxyFactory;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.meta.AssociationTable;
import persistence.sql.meta.Table;

import java.util.List;

public class MyEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;
    private final ProxyFactory proxyFactory;

    public MyEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = SelectQueryBuilder.getInstance();
        this.proxyFactory = new MyProxyFactory(jdbcTemplate);
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
        Object proxy = proxyFactory.createProxy(associationTable.getClazz());
        associationTable.getField().setAccessible(true);
        try {
            associationTable.getField().set(object, proxy);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            associationTable.getField().setAccessible(false);
        }
    }
}
