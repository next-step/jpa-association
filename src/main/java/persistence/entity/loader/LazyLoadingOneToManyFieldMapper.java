package persistence.entity.loader;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.attribute.id.IdAttribute;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.Optional;

public class LazyLoadingOneToManyFieldMapper implements MapperResolver {
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
    public <T> void map(T instance, Field field, ResultSet resultSet) {
        IdAttribute ownerIdAttribute = entityAttributes.findEntityAttribute(instance.getClass())
                .getIdAttribute();

        Class<?> fieldArgType = getCollectionFieldType(field);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(fieldArgType);
        enhancer.setCallback(new LazyLoader() {
            @Override
            public Object loadObject() throws Exception {
                Object targetClass = collectionLoader.loadCollection(fieldArgType, ownerIdAttribute.getColumnName(),
                        getInstanceIdAsString(instance, field));

                field.set(instance, targetClass);

                return targetClass;
            }
        });

        Object proxy = enhancer.create();
        try {
            field.set(instance, proxy);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
