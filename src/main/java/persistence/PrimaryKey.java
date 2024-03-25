package persistence;

import jakarta.persistence.Id;
import persistence.entity.exception.InvalidPrimaryKeyException;
import persistence.sql.exception.NotIdException;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class PrimaryKey {

    private final Field field;
    private final String name;
    private final String dataTypeName;

    public PrimaryKey(Class<?> clazz) {
        this.field = getIdField(clazz);
        this.name = field.getName();
        this.dataTypeName = field.getType().getSimpleName();
    }

    public <T> PrimaryKey(T entity) {
        this.field = getIdField(entity.getClass());
        this.name = field.getName();
        this.dataTypeName = field.getType().getSimpleName();
    }

    public <T> Long getPrimaryKeyValue(T entity) {
        Field idField = getIdField(entity.getClass());
        idField.setAccessible(true);
        try {
            return (Long) idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new InvalidPrimaryKeyException();
        }
    }

    public Field field() {
        return this.field;
    }

    public String name() {
        return this.name;
    }

    public String dataTypeName() {
        return this.dataTypeName;
    }

    private Field getIdField(Class<?> clazz) {
        return Stream.of(clazz.getDeclaredFields())
                .filter(x -> x.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(NotIdException::new);
    }
}
