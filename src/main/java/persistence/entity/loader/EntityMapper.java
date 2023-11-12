package persistence.entity.loader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import persistence.exception.NotFoundException;
import persistence.meta.ColumnType;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;

public abstract class EntityMapper {
    protected static final int ROOT_LEVEL_NUMBER = 0;

    protected final EntityMeta entityMeta;

    public EntityMapper(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    public <T> T findMapper(Class<T> tClass, ResultSet resultSet) {
        return resultSetToEntity(tClass, resultSet);
    }

    protected <T> T resultSetToEntity(Class<T> tClass, ResultSet resultSet) {
        return resultSetSingleToEntity(tClass, resultSet, ROOT_LEVEL_NUMBER);
    }

    protected <T> T resultSetSingleToEntity(Class<T> tClass, ResultSet resultSet, int level) {
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

    protected <T> void setFieldValue(T instance, String fieldName, Object value) {
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

    protected boolean isNextRow(ResultSet resultSet) {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
