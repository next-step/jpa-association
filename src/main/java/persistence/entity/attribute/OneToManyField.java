package persistence.entity.attribute;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

public class OneToManyField {
    private final Field field;
    private final String fieldName;
    private final String joinColumnName;
    private final FetchType fetchType;
    private final String ownerTableName;
    private final String ownerIdAttribute;
    private final EntityAttribute entityAttribute;

    public OneToManyField(Field field, String ownerTableName, String ownerIdAttribute, Set<Class<?>> visitedEntities) {
        this.field = field;
        this.ownerTableName = ownerTableName;
        this.ownerIdAttribute = ownerIdAttribute;
        this.fieldName = field.getName();
        this.fetchType = getFetchTypeFromField(field);
        this.joinColumnName = getJoinColumnName(field);
        this.entityAttribute = getEntityAttributeFromField(field, visitedEntities);
    }

    private FetchType getFetchTypeFromField(Field field) {
        jakarta.persistence.OneToMany oneToMany = field.getDeclaredAnnotation(jakarta.persistence.OneToMany.class);
        return oneToMany.fetch();
    }

    private String getJoinColumnName(Field field) {
        Optional<JoinColumn> joinColumnOptional = Optional.ofNullable(field.getDeclaredAnnotation(jakarta.persistence.JoinColumn.class));
        return joinColumnOptional.map(JoinColumn::name).orElse(null);
    }

    private EntityAttribute getEntityAttributeFromField(Field field, Set<Class<?>> visitedEntities) {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            throw new IllegalArgumentException(String.format("[%s] 제네릭 타입이 아닙니다.", field.getGenericType()));
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length != 1 || !(actualTypeArguments[0] instanceof Class)) {
            throw new IllegalArgumentException(String.format("[%s] 제네릭 타입은 꼭 하나 존재해야합니다.", actualTypeArguments[0]));
        }

        Class<?> fieldType = (Class<?>) actualTypeArguments[0];
        return EntityAttribute.of(fieldType, visitedEntities);
    }

    public EntityAttribute getEntityAttribute() {
        return entityAttribute;
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }

    public Field getField() {
        return field;
    }
}
