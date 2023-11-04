package jdbc;

import hibernate.entity.meta.EntityClass;
import hibernate.entity.meta.column.EntityColumn;
import hibernate.entity.meta.column.EntityJoinColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionRowMapper<T> implements RowMapper<T> {

    private static final Map<EntityClass<?>, ReflectionRowMapper<?>> CACHE = new ConcurrentHashMap<>();

    private final EntityClass<T> entityClass;

    public ReflectionRowMapper(EntityClass<T> entityClass) {
        this.entityClass = entityClass;
    }

    public static <T> ReflectionRowMapper<T> getInstance(final EntityClass<T> entityClass) {
        return (ReflectionRowMapper<T>) CACHE.computeIfAbsent(entityClass, ReflectionRowMapper::new);
    }

    @Override
    public T mapRow(final ResultSet resultSet) throws SQLException {
        T instance = entityClass.newInstance();
        List<EntityColumn> entityColumns = entityClass.getEntityColumns();
        setEachColumn(entityColumns, instance, resultSet, entityClass.tableName());

        List<EntityJoinColumn> eagerJoinColumns = entityClass.getEagerJoinColumn();
        if (eagerJoinColumns.isEmpty()) {
            return instance;
        }
        do {
            setEachJoinColumn(resultSet, eagerJoinColumns, instance);
        } while (resultSet.next());
        return instance;
    }

    private void setEachJoinColumn(
            final ResultSet resultSet,
            final List<EntityJoinColumn> eagerJoinColumns,
            final T instance) throws SQLException
    {
        for (EntityJoinColumn eagerJoinColumn : eagerJoinColumns) {
            EntityClass<?> eagerJoinClass = eagerJoinColumn.getEntityClass();

            Object subInstance = eagerJoinClass.newInstance();
            List<EntityColumn> subEntityColumns = eagerJoinClass.getEntityColumns();
            setEachColumn(subEntityColumns, subInstance, resultSet, eagerJoinClass.tableName());
            eagerJoinColumn.assignFieldValue(instance, subInstance);
        }
    }

    private void setEachColumn(
            final List<EntityColumn> subEntityColumns,
            final Object subInstance,
            final ResultSet resultSet,
            final String eagerJoinClass) throws SQLException
    {
        for (EntityColumn subEntityColumn : subEntityColumns) {
            subEntityColumn.assignFieldValue(subInstance, resultSet.getObject(eagerJoinClass + "." + subEntityColumn.getFieldName()));
        }
    }
}
