package persistence.entity.attribute;

import jakarta.persistence.Column;

import java.lang.reflect.Field;

public class LongTypeGeneralAttribute implements GeneralAttribute {
    private final Integer scale;
    private final String fieldName;
    private final String columnName;
    private final boolean nullable;
    private final Field field;

    public LongTypeGeneralAttribute(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);

        validate(field.getType(), column);

        this.field = field;
        this.scale = column.scale();
        this.fieldName = field.getName();
        this.columnName = column.name().isBlank() ? field.getName() : column.name();
        this.nullable = column.nullable();
    }

    private void validate(Class<?> type, Column column) {
        if (type != Long.class) {
            throw new IllegalArgumentException("Long 타입의 필드만 인자로 받을 수 있습니다.");
        }
        if (column == null) {
            throw new IllegalArgumentException("Column 어노테이션이 붙은 필드만 인자로 받을 수 있습니다.");
        }
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    public Integer getScale() {
        return scale;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Field getField() {
        return field;
    }
}
