package persistence.sql.mapping;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import persistence.sql.mapping.exception.GenerationTypeMissingException;

import java.lang.reflect.Field;

public class ColumnData {
    private final String name;
    private final int type;
    private Object value;
    private final boolean isPk;
    private final GenerationType generationType;
    private final boolean isNullable;

    private ColumnData(String name, int type, boolean isPk, GenerationType generationType, boolean isNullable) {
        this.name = name;
        this.type = type;
        this.isPk = isPk;
        this.generationType = generationType;
        this.isNullable = isNullable;
    }

    public static ColumnData createColumn(Field field) {
        Column column = field.getAnnotation(Column.class);
        return new ColumnData(
                extractName(field, column),
                extractDataType(field),
                extractIsPrimaryKey(field),
                extractGenerationType(field),
                extractIsNullable(column)
        );
    }

    public static ColumnData createColumnWithValue(Field field, Object object) {
        ColumnData columnData = createColumn(field);
        columnData.setValue(extractValue(field, object));
        return columnData;
    }

    public boolean isPrimaryKey() {
        return isPk;
    }

    public boolean isNotPrimaryKey() {
        return !isPk;
    }

    private void setValue(Object value) {
        this.value = value;
    }

    private static String extractName(Field field, Column column) {
        String columnName = field.getName();
        if (column != null && !column.name().isEmpty()) {
            columnName = column.name();
        }
        return columnName;
    }

    private static Object extractValue(Field field, Object entity) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static int extractDataType(Field field) {
        return new DataTypeMapper().mapToSqlType(field.getType());
    }

    private static boolean extractIsNullable(Column column) {
        if (column == null) {
            return true;
        }
        return column.nullable();
    }

    private static GenerationType extractGenerationType(Field field) {
        GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
        if (generatedValue == null) {
            return null;
        }
        return generatedValue.strategy();
    }

    private static boolean extractIsPrimaryKey(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public boolean isNotNullable() {
        return !isNullable;
    }

    public boolean hasGenerationType() {
        return generationType != null;
    }

    public GenerationType getGenerationType() {
        if(!hasGenerationType()) {
            throw new GenerationTypeMissingException();
        }
        return generationType;
    }
}
