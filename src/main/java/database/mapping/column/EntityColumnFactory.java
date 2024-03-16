package database.mapping.column;

import jakarta.persistence.Id;

import java.lang.reflect.Field;

public class EntityColumnFactory {
    public static EntityColumn fromField(Field field) {
        boolean isId = field.isAnnotationPresent(Id.class);
        if (isId) {
            return fromPrimaryKeyField(field);
        }
        return fromGeneralField(field);
    }

    private static PrimaryKeyEntityColumn fromPrimaryKeyField(Field field) {
        return PrimaryKeyEntityColumn.create(field);
    }

    public static GeneralEntityColumn fromGeneralField(Field field) {
        return GeneralEntityColumn.create(field);
    }
}
