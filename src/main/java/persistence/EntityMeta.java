package persistence;

import jakarta.persistence.Column;
import persistence.entity.CustomJoinTable;
import persistence.entity.UniqueColumn;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityMeta {
    private final Map<String, String> columnMap;
    private final CustomTable customTable;
    private final UniqueColumn uniqueColumn;
    private final CustomJoinTable customJoinTable;

    private EntityMeta(Map<String, String> columnMap, CustomTable customTable, UniqueColumn uniqueColumn, CustomJoinTable customJoinTable) {
        this.columnMap = columnMap;
        this.customTable = customTable;
        this.uniqueColumn = uniqueColumn;
        this.customJoinTable = customJoinTable;
    }

    public static EntityMeta of(Class<?> clazz) {
        Map<String, String> columnMap = Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toMap(EntityMeta::columnName, Field::getName));
        return new EntityMeta(columnMap, CustomTable.of(clazz), UniqueColumn.of(clazz), null);
    }

    public static EntityMeta ofJoin(Class<?> clazz) {
        Map<String, String> columnMap = Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toMap(EntityMeta::columnName, Field::getName));
        return new EntityMeta(columnMap, CustomTable.of(clazz), UniqueColumn.of(clazz), CustomJoinTable.of(clazz));
    }

    public String column(String alias) {
        return columnMap.get(alias.toLowerCase());
    }

    public String uniqueColumn(String tableName) {
        return String.format("%s.%s", tableName, uniqueColumn.name());
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

    public String joinTableName() {
        return customJoinTable.joinTable();
    }

    public String joinRootColumn() {
        return customJoinTable.rootColumn();
    }

    public String joinJoinColumn() {
        return customJoinTable.joinColumn();
    }

    public static boolean hasJoin(Class<?> clazz) {
        return CustomJoinTable.findTableFiled(clazz) != null;
    }
}
