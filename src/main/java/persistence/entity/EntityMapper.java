package persistence.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import persistence.association.EntityOneToManyPath;
import persistence.association.OneToManyAssociation;
import persistence.exception.NotFoundException;
import persistence.meta.ColumnType;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;

public class EntityMapper {
    private static final int ROOT_LEVEL_NUMBER = 0;
    private static final int ASSOCIATION_START_LEVEL = 1;
    private final EntityMeta entityMeta;


    public EntityMapper(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    public <T> T resultSetToEntity(Class<T> tClass, ResultSet resultSet) {
        if (!entityMeta.hasOneToManyAssociation()) {
            return resultSetSingleToEntity(tClass, resultSet);
        }

        final T instance = resultSetSingleToEntity(tClass, resultSet);
        final EntityOneToManyPath path = new EntityOneToManyPath(entityMeta, instance);

        loadManyEntity(path, resultSet);

        while (isNextRow(resultSet)) {
            loadManyEntity(path, resultSet);
        }

        return entityLoad(path, instance);
    }

    private <T> T entityLoad(EntityOneToManyPath path, T instance) {
        OneToManyAssociation oneToManyAssociation = entityMeta.getOneToManyAssociation();
        for (int level = ASSOCIATION_START_LEVEL; level < path.totalLevel(); level++) {
            setFieldValue(instance, oneToManyAssociation.getOneField().getName(), path.get(level));
            oneToManyAssociation = oneToManyAssociation.getManyEntityMeta().getOneToManyAssociation();
        }
        return instance;
    }

    public <T> List<T> resultSetToEntityAll(Class<T> tClass, ResultSet resultSet) {
        final Map<EntityKey, EntityOneToManyPath> context = new ConcurrentHashMap<>();
        List<T> list = new ArrayList<>();
        while (isNextRow(resultSet)) {
            final T instance = resultSetSingleToEntity(tClass, resultSet);
            final Object pkValue = entityMeta.getPkValue(instance);
            final EntityKey entityKey = EntityKey.of(tClass, pkValue);

            EntityOneToManyPath path = context.getOrDefault(entityKey, new EntityOneToManyPath(entityMeta, instance));
            context.put(entityKey, path);
            loadManyEntity(path, resultSet);
        }

        for (Entry<EntityKey, EntityOneToManyPath> entries : context.entrySet()) {
            EntityOneToManyPath path = entries.getValue();
            final Object entity = path.getRootInstance();
            list.add((T) entityLoad(path, entity));
        }

        return list;
    }

    private void loadManyEntity(EntityOneToManyPath path, ResultSet resultSet) {
        OneToManyAssociation oneToManyAssociation = entityMeta.getOneToManyAssociation();

        for (int level = ASSOCIATION_START_LEVEL; level < path.totalLevel(); level++) {
            final List<Object> list = path.get(level);
            list.add(resultSetSingleToEntity(oneToManyAssociation.getManyEntityMeta().getEntityClass(), resultSet,
                    level));
            oneToManyAssociation = oneToManyAssociation.getManyEntityMeta().getOneToManyAssociation();
        }
    }


    private <T> T resultSetSingleToEntity(Class<T> tClass, ResultSet resultSet) {
        return resultSetSingleToEntity(tClass, resultSet, ROOT_LEVEL_NUMBER);
    }


    <T> T resultSetSingleToEntity(Class<T> tClass, ResultSet resultSet, int level) {
        final T instance = getInstance(tClass);
        final EntityMeta entityMeta = EntityMeta.from(tClass);

        for (EntityColumn entityColumn : entityMeta.getEntityColumns()) {
            final String columnNameSignature = entityColumn.getColumnNameSignature(entityMeta.getTableName(), level);
            final Object resultSetColumn = getLoadValue(resultSet, entityColumn, columnNameSignature);
            setFieldValue(instance, entityColumn.getFieldName(), resultSetColumn);
        }
        return instance;
    }

    private static <T> T getInstance(Class<T> tClass) {
        try {
            return tClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            throw new NotFoundException(e);
        }
    }

    private static <T> Field getFiled(Class<T> tClass, String filedName) {
        try {
            return tClass.getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {
            throw new NotFoundException("필드를 찾을수 없습니다.");
        }
    }

    private <T> void setFieldValue(T instance, String fieldName, Object value) {
        try {
            final Field field = getFiled(instance.getClass(), fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private Object getLoadValue(ResultSet resultSet, EntityColumn column, String columName) {
        try {
            return getTypeValue(resultSet, column, columName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getTypeValue(ResultSet resultSet, EntityColumn column, String columName) throws SQLException {
        final ColumnType columType = column.getColumnType();
        if (columType.isBigInt()) {
            return resultSet.getLong(columName);
        }
        if (columType.isVarchar()) {
            return resultSet.getString(columName);
        }
        if (columType.isInteger()) {
            return resultSet.getInt(columName);
        }
        return null;
    }

    private boolean isNextRow(ResultSet resultSet) {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
