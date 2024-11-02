package builder.dml;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseEntityColumn {

    protected static final String GET_FIELD_VALUE_ERROR_MESSAGE = "필드 값을 가져오는 중 에러가 발생했습니다.";

    protected <T> List<DMLColumnData> getInstanceColumnData(T entityInstance, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(this::checkSkipAnnotation)
                .map(field -> getDmlColumnData(field, entityInstance))
                .collect(Collectors.toList());
    }

    protected List<DMLColumnData> getEntityColumnData(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(this::checkSkipAnnotation)
                .map(this::getDmlColumnData)
                .collect(Collectors.toList());
    }

    protected String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            return table.name();
        }
        return entityClass.getSimpleName();
    }

    @NotNull
    private static <T> DMLColumnData getDmlColumnData(Field field, T entityInstance) {
        if (field.isAnnotationPresent(Id.class)) {
            field.setAccessible(true);
            try {
                return DMLColumnData.createInstancePkColumn(field.getName(), field.getType(), field.get(entityInstance));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(GET_FIELD_VALUE_ERROR_MESSAGE + field.getName(), e);
            }
        }
        return getColumnData(field, entityInstance);
    }

    @NotNull
    private DMLColumnData getDmlColumnData(Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            return DMLColumnData.createEntityPkColumn(field.getName(), field.getType());
        }
        return getColumnData(field);
    }

    @NotNull
    private static <T> DMLColumnData getColumnData(Field field, T entityInstance) {
        String columnName = field.getName();
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name().isEmpty() ? columnName : column.name();
        }
        field.setAccessible(true);
        try {
            return DMLColumnData.creatInstanceColumn(columnName, field.getType(), field.get(entityInstance));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(GET_FIELD_VALUE_ERROR_MESSAGE + field.getName(), e);
        }
    }

    @NotNull
    private DMLColumnData getColumnData(Field field) {
        String columnName = field.getName();
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name().isEmpty() ? columnName : column.name();
        }
        return DMLColumnData.createEntityColumn(columnName);
    }

    private boolean checkSkipAnnotation(Field field) {
        return !field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(OneToMany.class);
    }

}