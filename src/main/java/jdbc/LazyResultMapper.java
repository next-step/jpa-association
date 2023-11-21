package jdbc;

import jakarta.persistence.Transient;
import net.sf.cglib.proxy.Enhancer;
import persistence.sql.common.meta.Column;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.JoinColumn;
import persistence.sql.common.meta.TableName;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LazyResultMapper<T> implements RowMapper<T> {
    private final Class<T> tClass;

    public LazyResultMapper(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        T clazz = getConstructor(tClass);

        extracted(resultSet, tClass, clazz);

        return clazz;
    }

    /**
     * resultSet의 타입을 객체 타입에 맞춰 가져옵니다.
     */
    private void extracted(Object object, ResultSet resultSet, Field field, String columnName) {
        try {
            if (isTypeEquals(field, List.class)) {
                Class<?> aClazz = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                JoinColumn joinColumn = JoinColumn.of(tClass.getDeclaredFields());

                TableName tableName = TableName.of(tClass);
                Columns columns = Columns.of(tClass.getDeclaredFields());

                Object value = resultSet.getObject(String.join(".", tableName.getName(), columns.getIdName()));

                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(ArrayList.class);
                enhancer.setCallback(new LazyInterceptor(value, aClazz, joinColumn));
                field.set(object, enhancer.create());

                return;
            }

            field.set(object, resultSet.getObject(columnName));
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <I> boolean isTypeEquals(Field field, Class<I> tClass) {
        return field.getType().equals(tClass);
    }

    private void extracted(ResultSet resultSet, Class clazz, Object object) {
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .forEach(field -> {
                    TableName tableName = TableName.of(object.getClass());
                    Column column = Column.of(field);

                    field.setAccessible(true);
                    extracted(object, resultSet, field, tableName.getName() + "." + column.getName());
                });
    }

    private T getConstructor(Class<T> tClass) {
        try {
            return tClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
