package database.mapping.column;

import jakarta.persistence.Id;

import java.lang.reflect.Field;

public class EntityColumnFactory {
    public static EntityColumn fromField(Field field) {
        boolean isId = field.isAnnotationPresent(Id.class);
        if (isId) {
            return PrimaryKeyEntityColumn.create(field);
        }
        return GeneralEntityColumn.create(field);
    }
}
