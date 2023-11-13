package jdbc;

import jakarta.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import persistence.sql.common.meta.Column;
import persistence.sql.common.meta.TableName;

public class ResultMapper<T> implements RowMapper<T> {

    private final Class<T> tClass;

    public ResultMapper(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        T clazz = getConstructor(tClass);

        extracted(resultSet, tClass, clazz);

        return clazz;
    }

    private void extracted(ResultSet resultSet, Class clazz, Object object) {
        Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Transient.class))
            .forEach(field -> {
                field.setAccessible(true);
                setFieldData(resultSet, field, object);
            });
    }

    /**
     * resultSet의 타입을 객체 타입에 맞춰 가져옵니다.
     */
    private <R> R extracted(ResultSet resultSet, Field field, Class<R> rClass, String columnName) {
        try {
            if (isTypeEquals(field, List.class)) {
                Class<?> aClazz = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

                List<Object> list = new ArrayList<>();

                while (!resultSet.isAfterLast()) {

                    Object o = aClazz.newInstance();

                    extracted(resultSet, aClazz, o);

                    resultSet.next();
                    list.add(o);
                }
                return (R) list;
            }

            return rClass.cast(resultSet.getObject(columnName, rClass));
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <I> boolean isTypeEquals(Field field, Class<I> tClass) {
        return field.getType().equals(tClass);
    }

    private T getConstructor(Class<T> tClass) {
        try {
            return tClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFieldData(ResultSet resultSet, Field field, Object object) {
        try {
            TableName tableName = TableName.of(object.getClass());
            Column column = Column.of(field);

            field.set(object,
                extracted(resultSet, field, field.getType(), tableName.getName() + "." + column.getName()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
