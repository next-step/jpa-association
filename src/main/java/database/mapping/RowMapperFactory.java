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

    public static RowMapper<Object> create(Constructor<?> declaredConstructor, Class<?> clazz, Dialect dialect) {
        return resultSet -> {
            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            try {
                Object object = declaredConstructor.newInstance();

                for (int i = 1; i < rsMetaData.getColumnCount() + 1; i++) {
                    String columnName = rsMetaData.getColumnName(i);
                    int columnType = rsMetaData.getColumnType(i);
                    try {
                        setFieldValue(resultSet, columnName, columnType, object, clazz, dialect);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                return object;
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static void setFieldValue(ResultSet resultSet,
                                      String columnName,
                                      int columnType,
                                      Object entity,
                                      Class<?> clazz,
                                      Dialect dialect) throws SQLException, IllegalAccessException {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        Object value = dialect.getFieldValueFromResultSet(resultSet, columnName, columnType);
        Field field = entityMetadata.getFieldByColumnName(columnName);
        field.setAccessible(true);
        field.set(entity, value);
    }
}
