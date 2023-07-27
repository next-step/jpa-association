package persistence.entity;

import jdbc.RowMapper;
import jdbc.exception.RowMapException;
import persistence.sql.util.ColumnFields;
import persistence.sql.util.ColumnName;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityLoader<T> implements RowMapper<T> {
    private final Class<T> clazz;
    private final Map<String, Field> fieldsByName;

    public EntityLoader(Class<T> clazz) {
        this.clazz = clazz;
        this.fieldsByName = ColumnFields.forQuery(clazz)
                .stream().collect(Collectors.toMap(
                        ColumnName::build,
                        Function.identity()
                ));
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        try {
            final T object = clazz.getDeclaredConstructor().newInstance();
            final ResultSetMetaData metaData = resultSet.getMetaData();
            final int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                Field field = fieldsByName.get(metaData.getColumnLabel(i));
                Object value = resultSet.getObject(metaData.getColumnName(i));
                field.setAccessible(true);
                field.set(object, value);
            }
            return object;
        } catch (Exception e) {
            throw new RowMapException(e);
        }
    }
}
