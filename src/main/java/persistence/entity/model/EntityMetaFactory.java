package persistence.entity.model;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import persistence.field.Fields;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityMetaFactory {

    public static final EntityMetaFactory INSTANCE = new EntityMetaFactory();

    public EntityMeta create(Class<?> clazz) {
        Fields tableFields = getTableFields(clazz);
        String tableName = tableName(clazz);
        EntityColumn idColumn = getIdColumn(tableFields);
        OneToManyColumn oneToManyColumn = getOneToManyColumn(tableFields);
        EntityColumns normalColumns = getNormalColumns(tableFields);

        return new EntityMeta(tableName, idColumn, normalColumns, oneToManyColumn);
    }

    private static EntityColumns getNormalColumns(Fields tableFields) {
        return tableFields.getFields(Id.class, OneToMany.class)
                .stream()
                .map(EntityColumn::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), EntityColumns::new));
    }

    private static Fields getTableFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Fields::new));
    }

    private String tableName(Class<?> clazz) {
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        if (tableAnnotation != null && !tableAnnotation.name().isBlank()) {
            return tableAnnotation.name();
        }
        return clazz.getSimpleName().toLowerCase();
    }

    private static EntityColumn getIdColumn(Fields tableFields) {
        return new EntityColumn(tableFields.getField(Id.class));
    }

    private static OneToManyColumn getOneToManyColumn(Fields tableFields) {
        Optional<Field> optionalField = tableFields.findField(OneToMany.class);
        return optionalField.map(OneToManyColumn::of).orElse(null);
    }
}
