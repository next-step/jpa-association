package persistence.entity.attribute;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.lang.reflect.Field;

public class IntegerTypeGeneralAttribute implements GeneralAttribute {
    private final boolean nullable;
    private final int scale;
    private final String fieldName;
    private final String columnName;
    private final Field field;

    public IntegerTypeGeneralAttribute(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);

        validate(field.getType(), column);
        this.field = field;
        this.scale = column.scale();
        this.fieldName = field.getName();
        this.columnName = column.name().isBlank() ? field.getName() : column.name();
        this.nullable = field.isAnnotationPresent(Id.class);
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public Field getField() {
        return this.field;
    }

    public boolean isNullable() {
        return nullable;
    }

    public int getScale() {
        return scale;
    }

    private void validate(Class<?> type, Column column) {
        if (type != Integer.class) {
            throw new IllegalArgumentException("Integer 타입의 필드만 인자로 받을 수 있습니다.");
        }
        if (column == null) {
            throw new IllegalArgumentException("Column 어노테이션이 붙은 필드만 인자로 받을 수 있습니다.");
        }
    }
}
