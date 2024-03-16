package database.mapping.column;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.lang.reflect.Field;

public class PrimaryKeyEntityColumn extends AbstractEntityColumn {
    private PrimaryKeyEntityColumn(Field field,
                                   String columnName,
                                   Class<?> type,
                                   Integer columnLength) {
        super(field, columnName, type, columnLength);
    }

    public static PrimaryKeyEntityColumn create(Field field) {
        String columnName = getColumnName(field);
        Class<?> type = field.getType();
        Integer columnLength = getColumnLength(field);

        return new PrimaryKeyEntityColumn(field, columnName, type, columnLength);
    }

    @Override
    public boolean isPrimaryKeyField() {
        return true;
    }

    public boolean isAutoIncrement() {
        GeneratedValue generatedValueAnnotation = field.getAnnotation(GeneratedValue.class);
        return generatedValueAnnotation != null && generatedValueAnnotation.strategy() == GenerationType.IDENTITY;
    }
}
