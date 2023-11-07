package hibernate.entity;

import hibernate.dml.SelectQueryBuilder;
import hibernate.entity.collection.PersistentList;
import hibernate.entity.meta.EntityClass;
import hibernate.entity.meta.column.EntityJoinColumn;
import hibernate.entity.meta.column.EntityJoinColumns;
import jdbc.JdbcTemplate;
import jdbc.ReflectionRowMapper;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;

import java.util.List;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;

    public EntityLoader(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T find(final EntityClass<T> entityClass, final Object id) {
        EntityJoinColumns entityJoinColumns = EntityJoinColumns.oneToManyColumns(entityClass);
        final String query = selectQueryBuilder.generateQuery(
                entityClass.tableName(),
                entityClass.getFieldNames(),
                entityClass.getEntityId(),
                id,
                entityJoinColumns.getEagerJoinTableFields(),
                entityJoinColumns.getEagerJoinTableIds()
        );
        T instance = jdbcTemplate.queryForObject(query, ReflectionRowMapper.getInstance(entityClass));

        if (entityJoinColumns.hasLazyFetchType()) {
            setLazyJoinColumns(entityJoinColumns.getLazyValues(), instance);
        }
        return instance;
    }

    public <T> List<T> findAll(final EntityClass<T> entityClass) {
        final String query = selectQueryBuilder.generateAllQuery(entityClass.tableName(), entityClass.getFieldNames());
        return jdbcTemplate.query(query, ReflectionRowMapper.getInstance(entityClass));
    }

    private <T> void setLazyJoinColumns(List<EntityJoinColumn> lazyJoinColumns, T instance) {
        for (EntityJoinColumn lazyJoinColumn : lazyJoinColumns) {
            Enhancer enhancer = generateEnhancer(lazyJoinColumn.getEntityClass());
            lazyJoinColumn.assignFieldValue(instance, enhancer.create());
        }
    }

    private <T> Enhancer generateEnhancer(EntityClass<T> entityClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(List.class);
        enhancer.setCallback((LazyLoader) () -> new PersistentList<>(entityClass, EntityLoader.this));
        return enhancer;
    }
}
