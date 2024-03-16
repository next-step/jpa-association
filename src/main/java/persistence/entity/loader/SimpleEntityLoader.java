package persistence.entity.loader;

import java.util.List;
import java.util.Map;
import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.proxy.LazyLoadingProxyFactory;
import persistence.sql.dml.DmlGenerator;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class SimpleEntityLoader implements EntityLoader {

    private final EntityManager entityManager;
    private final DmlGenerator dmlGenerator;

    private SimpleEntityLoader(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.dmlGenerator = DmlGenerator.getInstance();
    }

    public static SimpleEntityLoader from(EntityManager entityManager) {
        return new SimpleEntityLoader(entityManager);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T t =  entityManager.getJdbcTemplate().queryForObject(dmlGenerator.generateSelectQuery(clazz, id),
            resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));

        setLazyRelationProxy(Table.getInstance(clazz).getLazyRelationColumns(), t);
        return t;
    }

    @Override
    public <T> List<T> find(Class<T> clazz, Map<Column, Object> conditions) {
        return entityManager.getJdbcTemplate().query(dmlGenerator.generateSelectQuery(clazz, conditions),
            resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));
    }

    private void setLazyRelationProxy(List<Column> lazyRelationColumns, Object entity) {
        for (Column lazyRelationColumn : lazyRelationColumns) {
            lazyRelationColumn.setFieldValue(entity, LazyLoadingProxyFactory.create(Table.getInstance(entity.getClass()),
                lazyRelationColumn.getRelationTable(), entity, this, entityManager));
        }
    }
}
