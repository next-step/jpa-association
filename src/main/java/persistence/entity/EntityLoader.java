package persistence.entity;

import jdbc.RowMapper;
import jdbc.exception.RowMapException;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Map;

public class EntityLoader<T> implements RowMapper<T> {
    private final Class<T> clazz;
    private final EntityMeta meta;

    public EntityLoader(Class<T> clazz) {
        this.clazz = clazz;
        this.meta = new EntityMeta(clazz);
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        try {
            final T object = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Field> entry : meta.collectColumnFields().entrySet()) {
                String columnName = entry.getKey();
                Field field = entry.getValue();
                Object value = resultSet.getObject(columnName);
                field.setAccessible(true);
                field.set(object, value);
            }
            return object;
        } catch (Exception e) {
            throw new RowMapException(e);
        }
    }
}
