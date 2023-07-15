package persistence.entity;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;

public class UniqueColumn {
    private final String columnName;

    public UniqueColumn(String name) {
        this.columnName = name;
    }

    public static <T> UniqueColumn of(Class<T> clazz) {
        Field uniqueField = UniqueColumn.unique(clazz.getDeclaredFields());
        return new UniqueColumn(uniqueField.getName());
    }

    private static Field unique(Field[] field) {
        return Arrays.stream(field)
                .filter(it -> it.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(RuntimeException::new);

    }

    public String name() {
        return columnName;
    }
}
