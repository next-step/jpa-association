package persistence.sql.entity;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityColumns {
    private final List<EntityColumn> columns;

    public EntityColumns(List<EntityColumn> columns) {
        this.columns = columns;
    }

    public static EntityColumns from(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map(EntityColumn::from)
                .collect(Collectors.collectingAndThen(Collectors.toList(), EntityColumns::new));
    }

    public List<EntityColumn> getColumns() {
        return columns;
    }

    public String getIdFieldName() {
        for (EntityColumn column : columns) {
            if (column.isPrimaryKey()) {
                return column.getColumnName();
            }
        }
        throw new IllegalArgumentException("@Id 어노테이션이 존재하지 않음");
    }

    public List<EntityColumn> getOneToManyColumns() {
        return columns.stream()
                .filter(EntityColumn::isOneToMany)
                .collect(Collectors.toList());
    }

}
