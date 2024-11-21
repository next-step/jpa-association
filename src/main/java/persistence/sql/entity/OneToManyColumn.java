package persistence.sql.entity;

import jakarta.persistence.JoinColumn;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class OneToManyColumn {
    private final Field field;
    private Class<?> joinEntityClass;

    public OneToManyColumn(Field field) {
        this.field = field;
        this.joinEntityClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    public Class<?> getJoinEntityClass() {
        return joinEntityClass;
    }

    public String getForeignKeyColumnName() {
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        if (joinColumn != null) {
            return joinColumn.name();
        }
        return field.getName() + "_id";
    }

}
