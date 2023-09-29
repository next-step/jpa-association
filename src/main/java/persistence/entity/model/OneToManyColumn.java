package persistence.entity.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class OneToManyColumn {
    private final Field field;
    private final Class<?> entityClass;
    private final FetchType fetchType;

    public OneToManyColumn(Field field, Class<?> entityClass, FetchType fetchType) {
        this.field = field;
        this.entityClass = entityClass;
        this.fetchType = fetchType;
    }

    public static OneToManyColumn of(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type type = parameterizedType.getActualTypeArguments()[0];
            return new OneToManyColumn(field, (Class<?>) type, field.getAnnotation(OneToMany.class).fetch());
        }
        throw new IllegalArgumentException("잘못된 타입입니다");
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public Boolean isLazy() {
        return fetchType == FetchType.LAZY;
    }

    public Boolean isEager() {
        return fetchType == FetchType.EAGER;
    }

    public String getForeignKeyName() {
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        return joinColumn.name();
    }
}
