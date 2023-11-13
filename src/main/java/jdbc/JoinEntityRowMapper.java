package jdbc;

import persistence.sql.meta.ColumnMeta;
import persistence.sql.meta.EntityMeta;
import persistence.sql.meta.MetaFactory;
import persistence.sql.util.StringConstant;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Arrays;

public class JoinEntityRowMapper<T> implements RowMapper<T> {

    private final Class<T> clazz;
    private final EntityMeta entityMeta;

    public JoinEntityRowMapper(Class<T> clazz) {
        this.clazz = clazz;
        this.entityMeta = MetaFactory.get(clazz);
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T entityInstance = constructor.newInstance();
            Arrays.stream(clazz.getDeclaredFields())
                    .forEach(field -> setFieldValue(entityInstance, resultSet, field));
            return entityInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setFieldValue(T entityInstance, ResultSet resultSet, Field field) {
        ColumnMeta columnMeta = ColumnMeta.of(field);
        if (columnMeta.isTransient()) {
            return;
        }
        if (columnMeta.isJoinColumn()) {
            return;
        }
        field.setAccessible(true);
        try {
            Object fieldValue = resultSet.getObject(entityMeta.getTableName() + StringConstant.DOT + columnMeta.getColumnName());
            field.set(entityInstance, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
