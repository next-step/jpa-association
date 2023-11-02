package persistence.entity;

import persistence.core.*;
import persistence.exception.PersistenceException;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EntityRowMapper<T> {
    private final Class<T> clazz;
    private final EntityColumns columns;

    public EntityRowMapper(final Class<T> clazz) {
        this.clazz = clazz;
        this.columns = EntityMetadataProvider.getInstance().getEntityMetadata(clazz).getColumns();
    }

    public T mapRow(final ResultSet resultSet) {
        try {
            final T instance = ReflectionUtils.createInstance(clazz);

            for (final EntityColumn column : columns) {
                mapColumn(resultSet, instance, column);
            }

            for (final EntityOneToManyColumn entityOneToManyColumn : columns.getOneToManyColumns()) {
                mapOneToManyColumn(resultSet, instance, entityOneToManyColumn);
            }

            return instance;
        } catch (final SQLException e) {
            throw new PersistenceException("ResultSet Mapping 중 에러가 발생했습니다.", e);
        }
    }

    private void mapColumn(final ResultSet resultSet, final T instance, final EntityColumn column) throws SQLException {
        if (column.isOneToMany()) {
            return;
        }
        final String fieldName = column.getFieldName();
        final String columnName = column.getName();
        final Object object = resultSet.getObject(columnName);
        ReflectionUtils.injectField(instance, fieldName, object);
    }

    private void mapOneToManyColumn(final ResultSet resultSet, final T instance, final EntityOneToManyColumn entityOneToManyColumn) throws SQLException {
        final Collection<Object> innerCollection = createCollectionBy(entityOneToManyColumn.getType());
        final EntityMetadata<?> innerEntityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(entityOneToManyColumn.getJoinColumnType());

        do {
            final Object innerInstance = ReflectionUtils.createInstance(entityOneToManyColumn.getJoinColumnType());

            for (final EntityColumn innerEntityColumn : innerEntityMetadata.getColumns()) {
                final String fieldName = innerEntityColumn.getFieldName();
                final String columnName = innerEntityColumn.getName();
                final Object object = resultSet.getObject(innerEntityMetadata.getTableName() + "." + columnName);
                ReflectionUtils.injectField(innerInstance, fieldName, object);
            }

            innerCollection.add(innerInstance);
            ReflectionUtils.injectField(instance, entityOneToManyColumn.getFieldName(), innerCollection);
        } while (resultSet.next());
    }

    private Collection<Object> createCollectionBy(final Class<?> type) {
        if (type.isAssignableFrom(List.class)) {
            return new ArrayList<>();
        }
        if (type.isAssignableFrom(Set.class)) {
            return new LinkedHashSet<>();
        }
        throw new PersistenceException(type.getName() + "은 지원하지 않는 컬렉션 타입입니다.");
    }
}
