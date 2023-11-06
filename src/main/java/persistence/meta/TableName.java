package persistence.meta;

import jakarta.persistence.Table;

public class TableName {
    private final String value;
    private TableName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("테이블 이름은 null이거나 공백일 수 없습니다.");
        }
        this.value = value;
    }
    public static TableName from(Class<?> entityClass) {
        if (hasTableAnnotation(entityClass)) {
            return new TableName(entityClass.getSimpleName());
        }
        return new TableName(entityClass.getAnnotation(Table.class).name());
    }

    private static boolean hasTableAnnotation(Class<?> entityClass) {
        return !entityClass.isAnnotationPresent(Table.class) || entityClass.getAnnotation(Table.class).name().isBlank();
    }

    public String getValue() {
        return value;
    }
}
