package persistence.entity.loader;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import persistence.entity.attribute.OneToManyField;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EagerLoadingOneToManyFieldMapper implements CollectionMapperResolver {
    private final List<MapperResolver> MAPPER_RESOLVERS;

    public EagerLoadingOneToManyFieldMapper(List<MapperResolver> mapperResolvers) {
        this.MAPPER_RESOLVERS = mapperResolvers;
    }

    @Override
    public Boolean supports(Field field) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            return field.getAnnotation(OneToMany.class).fetch() == FetchType.EAGER;
        }
        return false;
    }

    @Override
    public void map(Object instance, OneToManyField oneToManyField, ResultSet resultSet) {
        Field field = oneToManyField.getField();
        Class<?> fieldArgType = getCollectionFieldType(field);

        try {
            field.setAccessible(true);

            Collection<Object> collection = getOrCreateCollection(instance, field);
            Object oneToManyInstance = mapResultSetToOneToManyAnnotatedField(resultSet, fieldArgType);
            collection.add(oneToManyInstance);
        } catch (Exception e) {
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

    private Collection<Object> getOrCreateCollection(Object entityInstance, Field field) throws IllegalAccessException {
        Collection<Object> collection = (Collection<Object>) field.get(entityInstance);
        if (collection == null) {
            collection = new ArrayList<>();
            field.set(entityInstance, collection);
        }
        return collection;
    }

    private Object mapResultSetToOneToManyAnnotatedField(ResultSet resultSet, Class<?> oneToManyFieldClass) {
        try {
            Object instance = oneToManyFieldClass.getConstructor().newInstance();

            for (Field field : oneToManyFieldClass.getDeclaredFields()) {
                for (MapperResolver mapperResolver : MAPPER_RESOLVERS) {
                    if (mapperResolver.supports(field)) {
                        mapperResolver.map(instance, field, resultSet);
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
