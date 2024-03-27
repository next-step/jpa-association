package persistence.model;

import java.lang.reflect.Field;

public class EntityId extends AbstractEntityField {

    protected EntityId(final String fieldName, final String columnName, final Class<?> entityClass, final Field field) {
        super(fieldName, columnName, entityClass, field);
    }

}
