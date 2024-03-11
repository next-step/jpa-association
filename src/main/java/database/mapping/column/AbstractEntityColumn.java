package database.mapping.column;

import jakarta.persistence.Column;

import java.lang.reflect.Field;

public abstract class AbstractEntityColumn implements EntityColumn {
    private static final int DEFAULT_COLUMN_LENGTH = 255;

    protected final Field field;
    protected final String fieldName;
    protected final String columnName;
    protected final Class<?> type;
    protected final Integer columnLength;

    public AbstractEntityColumn(Field field,
                                String columnName,
                                Class<?> type,
                                Integer columnLength) {
        this.field = field;
        this.fieldName = field.getName();
        this.columnName = columnName;
        this.type = type;
        this.columnLength = columnLength;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public Object getValue(Object entity) {
        field.setAccessible(true);
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected static Integer getColumnLength(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null) {
            return columnAnnotation.length();
        }
        return DEFAULT_COLUMN_LENGTH;
    }

    protected static String getColumnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null && !columnAnnotation.name().isEmpty()) {
            return columnAnnotation.name();
        }
        return field.getName();
    }
}
