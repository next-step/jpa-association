package persistence.entity.loader;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.attribute.OneToManyField;
import persistence.entity.attribute.id.IdAttribute;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LazyLoadingOneToManyFieldMapper implements CollectionMapperResolver {
    private final EntityAttributes entityAttributes;
    private final CollectionLoader collectionLoader;

    public LazyLoadingOneToManyFieldMapper(EntityAttributes entityAttributes, CollectionLoader collectionLoader) {
        this.entityAttributes = entityAttributes;
        this.collectionLoader = collectionLoader;
    }

    @Override
    public Boolean supports(Field field) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            return field.getAnnotation(OneToMany.class).fetch() == FetchType.LAZY;
        }
        return false;
    }

    @Override
    public <T> void map(T instance, OneToManyField oneToManyField, ResultSet resultSet) {
        Field field = oneToManyField.getField();

        EntityAttribute oneToManyFieldEntityAttribute = oneToManyField.getEntityAttribute();

        IdAttribute ownerIdAttribute = entityAttributes.findEntityAttribute(instance.getClass())
                .getIdAttribute();

        if (List.class.isAssignableFrom(field.getType())) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(ArrayList.class);
            enhancer.setCallback((LazyLoader) () -> collectionLoader.loadCollection(
                    oneToManyFieldEntityAttribute, oneToManyField.getJoinColumnName(),
                    getInstanceIdAsString(instance, ownerIdAttribute.getField())));

            List proxyList = (List) enhancer.create();
            try {
                field.setAccessible(true);
                field.set(instance, proxyList);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("OneToMany 관계는 List 타입만 지원합니다.");
        }
    }

    private Class<?> getCollectionFieldType(Field field) {
        try {
            Type genericFieldType = field.getGenericType();

            if (genericFieldType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericFieldType;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class<?>) {
                    return (Class<?>) actualTypeArguments[0];
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("제네릭 타입을 찾지 못했습니다.");
    }

    private <T> String getInstanceIdAsString(T instance, Field idField) {
        idField.setAccessible(true);

        try {
            return Optional.ofNullable(idField.get(instance)).map(String::valueOf).orElse(null);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
