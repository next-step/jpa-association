package jdbc.mapper;

import jakarta.persistence.Transient;
import persistence.sql.metadata.Column;
import persistence.sql.metadata.Table;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneToManyEntityMapper<T> implements RowMapper<T>{
    private final Class<T> clazz;

    public OneToManyEntityMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        if(!resultSet.next()) {
            return null;
        }

        try {
            T entity = clazz.getDeclaredConstructor().newInstance();

            return clazz.cast(setFields(clazz.getDeclaredFields(), entity, resultSet));
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    private Object setFields(Field[] declaredFields, Object entity, ResultSet resultSet) throws Exception {
        Field[] fields = Arrays.stream(declaredFields)
                .filter(x -> !x.isAnnotationPresent(Transient.class))
                .toArray(Field[]::new);

        for(Field field : fields) {
            field.setAccessible(true);
            field.set(entity, getFieldValue(field, entity, resultSet));
        }

        return entity;
    }

    private Object getFieldValue(Field field, Object entity, ResultSet resultSet) throws Exception{
        Column column = new Column(field);
        Table table = new Table(entity.getClass());

        if(column.hasAssociation()) {
            return getListFieldValue(column.getAssociation().getType(), resultSet);
        }

        return resultSet.getObject(table.getName() + "." + column.getName());
    }

    private List<Object> getListFieldValue(Class<?> clazz, ResultSet resultSet) throws Exception{
        List<Object> list = new ArrayList<>();

        while(!resultSet.isAfterLast()) {
            list.add(setFields(clazz.getDeclaredFields(), clazz.getDeclaredConstructor().newInstance(), resultSet));
            resultSet.next();
        }

        return list;
    }
}
