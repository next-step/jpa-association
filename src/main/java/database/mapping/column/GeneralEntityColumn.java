package database.mapping.column;

import jakarta.persistence.Column;

import java.lang.reflect.Field;

public class GeneralEntityColumn extends AbstractEntityColumn {
    private static final boolean DEFAULT_NULLABLE = true;

    private final boolean nullable;

    private GeneralEntityColumn(Field field,
                                String columnName,
                                Class<?> type,
                                Integer columnLength,
                                boolean nullable) {
        super(field, columnName, type, columnLength);
        this.nullable = nullable;
    }

    public static GeneralEntityColumn create(Field field) {
        String columnName = getColumnName(field);
        Integer columnLength = getColumnLength(field);
        Class<?> type = field.getType();
        boolean nullable = isNullable(field);

        return new GeneralEntityColumn(field, columnName, type, columnLength, nullable);
    }

    public boolean isNullable() {
        return nullable;
    }

    @Override
    public boolean isPrimaryKeyField() {
        return false;
    }

    private static boolean isNullable(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null) {
            return columnAnnotation.nullable();
        }
        return DEFAULT_NULLABLE;
    }
}
