package persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jdbc.RowMapper;
import persistence.CustomTable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                String tableName = resultSet.getMetaData().getTableName(i + 1);

                String alias = meta.getColumnLabel(i + 1);
                String name = meta.getColumnName(i + 1);


                if (tableName.equals(CustomTable.of(targetType).name().toUpperCase())) {
                    Field field = targetType.getDeclaredField(columnMap.get(alias.toLowerCase()));
                    field.setAccessible(true);

                    Object value = resultSet.getObject(name);
                    field.set(targetObject, value);
                }
            }

            List<Object> items = null;
            Field joinField = null;
            for (Field field : targetType.getDeclaredFields()) {
                if (field.isAnnotationPresent(OneToMany.class)) {
                    Class<?> fieldType = field.getType();
                    if (List.class.isAssignableFrom(fieldType)) {
                        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        Class<?> listItemType = (Class<?>) genericType.getActualTypeArguments()[0];

                        if (items == null) {
                            items = makeOneToMany(resultSet, listItemType);
                            joinField = field;
                        } else {
                            items.addAll(makeOneToMany(resultSet, listItemType));
                        }

                    }
                }
            }

            if (joinField != null) {
                joinField.setAccessible(true);
                joinField.set(targetObject, items);
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

    private List<Object> makeOneToMany(ResultSet resultSet, Class<?> listItemType) throws SQLException {
        List<Object> items = new ArrayList<>();
        boolean isLoop = true;
        while (isLoop) {
            Object item;
            try {
                item = listItemType.getDeclaredConstructor().newInstance();
                mapDataFields(item, resultSet);
                items.add(item);

                if (!resultSet.next()) {
                    isLoop = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    private void mapDataFields(Object object, ResultSet resultSet) throws SQLException {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            String columnName = field.getName();
            try {
                Object value = resultSet.getObject(columnName);
                field.setAccessible(true);
                field.set(object, value);
            } catch (Exception e) {
                continue;
            }
        }
    }
}
