package persistence.model;

import java.lang.reflect.Field;

public class EntityField {

    private final String fieldName;
    private final Class<?> entityClass;
    private final Field field;

    public EntityField(String fieldName, Class<?> entityClass, Field field) {
        this.fieldName = fieldName;
        this.entityClass = entityClass;
        this.field = field;
    }

    public Field getField() {
        return this.field;
    }

}
