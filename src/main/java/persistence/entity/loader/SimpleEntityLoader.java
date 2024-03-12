package persistence.entity.loader;

import java.util.List;
import java.util.Map;
import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.entity.proxy.LazyLoadingProxyFactory;
import persistence.sql.dml.DmlGenerator;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class SimpleEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;

    private SimpleEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = DmlGenerator.getInstance();
    }

    public static SimpleEntityLoader from(JdbcTemplate jdbcTemplate) {
        return new SimpleEntityLoader(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T t =  jdbcTemplate.queryForObject(dmlGenerator.generateSelectQuery(clazz, id),
            resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));

        Table table = Table.getInstance(clazz);
        setLazyRelationColumns(table.getLazyRelationColumns(), t);

        return t;
    }

    @Override
    public <T> List<T> find(Class<T> clazz, Map<Column, Object> conditions) {
        return jdbcTemplate.query(dmlGenerator.generateSelectQuery(clazz, conditions),
            resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));
    }

    private <T> void setLazyRelationColumns(List<Column> lazyRelationColumns, T instance) {
        for (Column lazyRelationColumn : lazyRelationColumns) {
            lazyRelationColumn.setFieldValue(instance, LazyLoadingProxyFactory.create(Table.getInstance(instance.getClass()),
                lazyRelationColumn.getRelationTable(), instance, this));
        }
    }
}
