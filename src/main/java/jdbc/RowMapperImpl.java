package jdbc;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RowMapperImpl<T> implements RowMapper<T> {
    private final Class<T> clazz;

    public RowMapperImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        T entity = getEntity();
        Field[] declaredFields = clazz.getDeclaredFields();

        Arrays.stream(declaredFields).forEach(field -> setField(field, entity, resultSet));
        return entity;
    }

    private T getEntity() {
        T entity;
        try {
            entity = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return entity;
    }

    private void setField(Field field, T entity, ResultSet resultSet) {
        if (field.isAnnotationPresent(Transient.class) || isLazyLoadingField(field)) {
            return;
        }

        field.setAccessible(true);
        try {
            if (isEagerLoadingField(field)) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                Class<?> collectionGenericType = (Class<?>) genericType.getActualTypeArguments()[0];
                List<Object> items = getOneToMany(resultSet, collectionGenericType);
                field.set(entity, items);
            } else {
                String columnName = getName(field);
                Object object = resultSet.getObject(columnName);
                field.set(entity, object);
            }
        } catch (IllegalAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isLazyLoadingField(Field field) {
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        return oneToMany != null && oneToMany.fetch() == FetchType.LAZY;
    }

    private static boolean isEagerLoadingField(Field field) {
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        return oneToMany != null && oneToMany.fetch() == FetchType.EAGER;
    }

    private List<Object> getOneToMany(ResultSet resultSet, Class<?> collectionGenericType) {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(collectionGenericType);
        Field[] declaredFields = collectionGenericType.getDeclaredFields();
        String tableName = entityMeta.getTableName();
        List<Object> elements = new ArrayList<>();

        try {
            do {
                Object element = collectionGenericType.getDeclaredConstructor().newInstance();
                for (Field declaredField : declaredFields) {
                    declaredField.setAccessible(true);
                    declaredField.set(element, resultSet.getObject(tableName + "." + getName(declaredField)));
                }
                elements.add(element);
            } while (resultSet.next());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return elements;
    }

    public String getName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column == null || column.name().isBlank()) {
            return field.getName();
        }

        return column.name();
    }
}
