package database.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

// TODO: 테스트 추가
public class RowMap<T> {
    private final Class<T> clazz;
    private final Map<String, Object> map;

    public RowMap(Class<T> clazz) {
        this.clazz = clazz;
        this.map = new HashMap<>();
    }

    public void add(String tableName, String columnName, Object value) {
        map.put(mapKey(tableName, columnName), value);
    }

    public T getParentEntity() {
        return mapValues(clazz);
    }

    public <R> R mapValues(Class<R> clazz) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);

        R entity = initiate(clazz);
        for (String columnName : entityMetadata.getAllColumnNames()) {
            setField(entity, columnName);
        }
        return entity;
    }

    private <R> R initiate(Class<R> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private <R> void setField(R entity, String columnName) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(entity.getClass());
        String tableName = entityMetadata.getTableName();
        Object value = map.get(mapKey(tableName, columnName));

        setFieldValue(entity, columnName, value);
    }

    private <R> void setFieldValue(R entity, String columnName, Object value) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(entity.getClass());

        Field field = entityMetadata.getFieldByColumnName(columnName);
        field.setAccessible(true);
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static String mapKey(String tableName, String columnName) {
        return (tableName + "." + columnName).toUpperCase();
    }
}
