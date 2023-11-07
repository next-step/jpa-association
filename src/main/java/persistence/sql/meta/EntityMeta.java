package persistence.sql.meta;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import persistence.sql.util.StringUtils;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class EntityMeta {

    private final Class<?> clazz;
    private final ColumnMetas columnMetas;

    private EntityMeta(Class<?> clazz, ColumnMetas columnMetas) {
        this.clazz = clazz;
        this.columnMetas = columnMetas;
    }

    public static EntityMeta of(Class<?> clazz) {
        return new EntityMeta(clazz, ColumnMetas.of(clazz.getDeclaredFields()));
    }

    public boolean isEntity() {
        return clazz.isAnnotationPresent(Entity.class);
    }

    public String getTableName() {
        Table tableAnnotation = clazz.getDeclaredAnnotation(Table.class);
        if (tableAnnotation != null && !StringUtils.isNullOrEmpty(tableAnnotation.name())) {
            return tableAnnotation.name();
        }
        return clazz.getSimpleName().toLowerCase();
    }

    public String getPkColumnName() {
        ColumnMetas idColumns = columnMetas.idColumns();
        Stream<ColumnMeta> columnMetaStream = StreamSupport.stream(idColumns.spliterator(), false);
        return columnMetaStream
                .findAny()
                .map(ColumnMeta::getColumnName)
                .orElseThrow();
    }

    public Class<?> getInnerClass() {
        return clazz;
    }

    public ColumnMetas getColumnMetas() {
        return columnMetas;
    }
}
