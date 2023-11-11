package jdbc;

import jakarta.persistence.Transient;
import persistence.sql.metadata.Column;
import persistence.sql.metadata.Table;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityMapper<T> implements RowMapper<T>{
    private final Class<T> clazz;

    public EntityMapper(Class<T> clazz) {
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
            Column column = new Column(field, null);
            Table table = new Table(entity.getClass());

            if(column.hasAssociation()) {
                if(field.getType().equals(List.class)) {
                    List<Object> list = new ArrayList<>();

                    while(!resultSet.isAfterLast()) {
                        Object joinEntity = column.getAssociation().getType().getDeclaredConstructor().newInstance();

                        list.add(setFields(joinEntity.getClass().getDeclaredFields(), joinEntity, resultSet));
                        resultSet.next();
                    }

                    field.setAccessible(true);
                    field.set(entity, list);
                }
            } else {
                field.setAccessible(true);
                field.set(entity, resultSet.getObject(table.getName() + "." + column.getName()));
            }
        }

        return entity;
    }
}
