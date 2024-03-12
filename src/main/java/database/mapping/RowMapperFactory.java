package database.mapping;

import database.dialect.Dialect;
import jdbc.RowMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class RowMapperFactory {
    private RowMapperFactory() {
    }

    public static <T> RowMapper<T> create(Class<T> clazz, Dialect dialect) {
        Constructor<?> declaredConstructor = getConstructor(clazz);

        return resultSet -> {
            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            try {
                T object = (T) declaredConstructor.newInstance();

                for (int i = 1; i < rsMetaData.getColumnCount() + 1; i++) {
                    String columnName = rsMetaData.getColumnName(i);
                    int columnType = rsMetaData.getColumnType(i);
                    setFieldValue(resultSet, columnName, columnType, object, clazz, dialect);
                }
                return object;
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Constructor<?> getConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setFieldValue(ResultSet resultSet,
                                      String columnName,
                                      int columnType,
                                      Object entity,
                                      Class<?> clazz,
                                      Dialect dialect) throws SQLException {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        Object value = dialect.getFieldValueFromResultSet(resultSet, columnName, columnType);

        Field field = entityMetadata.getFieldByColumnName(columnName);
        field.setAccessible(true);
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
