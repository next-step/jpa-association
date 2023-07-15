package persistence;

import jakarta.persistence.Column;
import persistence.entity.UniqueColumn;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityMeta {
    private final Map<String, String> columnMap;
    private final CustomTable customTable;
    private final UniqueColumn uniqueColumn;

    private EntityMeta(Map<String, String> columnMap, CustomTable customTable, UniqueColumn uniqueColumn) {
        this.columnMap = columnMap;
        this.customTable = customTable;
        this.uniqueColumn = uniqueColumn;
    }

    public static EntityMeta of(Class<?> clazz) {
        Map<String, String> columnMap = Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toMap(EntityMeta::columnName, Field::getName));
        return new EntityMeta(columnMap, CustomTable.of(clazz), UniqueColumn.of(clazz));
    }

    public String column(String alias) {
        return columnMap.get(alias.toLowerCase());
    }

    public String uniqueColumn() {
        return uniqueColumn.name();
    }

    public String tableName() {
        return customTable.name();
    }

    private static String columnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);

        if (columnAnnotation == null) {
            return field.getName();
        }

        if (columnAnnotation.name().equals("")) {
            return field.getName();
        }

        return columnAnnotation.name();
    }
}
