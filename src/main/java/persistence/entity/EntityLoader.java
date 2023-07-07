package persistence.entity;

import jakarta.persistence.Column;
import jdbc.RowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityLoader<T> implements RowMapper<T> {
    private final Class<T> targetType;
    private final Map<String, String> columnMap;

    public EntityLoader(Class<T> targetType) {
        this.targetType = targetType;
        this.columnMap = Arrays.stream(targetType.getDeclaredFields())
                .collect(Collectors.toMap(this::columnName, Field::getName));

    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();
        int columnCount = meta.getColumnCount();
        T targetObject = null;
        try {
            targetObject = targetType.getDeclaredConstructor().newInstance();
            
            for (int i = 0; i < columnCount; i++) {
                String alias = meta.getColumnLabel(i + 1);
                String name = meta.getColumnName(i + 1);
                Field field = targetType.getDeclaredField(columnMap.get(alias.toLowerCase()));
                field.setAccessible(true);

                Object value = resultSet.getObject(name);
                field.set(targetObject, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetObject;
    }

    private String columnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);

        if (columnAnnotation == null) {
            return field.getName();
        }

        if (columnAnnotation.name().equals("")) {
            return field.getName();
        }

        return columnAnnotation.name();
    }
}
