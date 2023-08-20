package persistence.entity.model;

import jakarta.persistence.JoinColumn;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class OneToManyColumn {
    private final Field field;

    private final Class<?> genericClass;

    private OneToManyColumn(Field field, Class<?> genericClass) {
        this.field = field;
        this.genericClass = genericClass;
    }

    public static OneToManyColumn of(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type type = parameterizedType.getActualTypeArguments()[0];
            return new OneToManyColumn(field, (Class<?>) type);
        }
        throw new IllegalArgumentException("잘못된 타입입니다");
    }

    public EntityMeta getEntityMeta() {
        return EntityMetaFactory.INSTANCE.create(genericClass);
    }

    public String getForeignKeyName() {
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        return joinColumn.name();
    }
}
