package persistence.entity.loader;

import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static persistence.entity.loader.MapperResolverHolder.MAPPER_RESOLVERS;

public class OneToManyFieldMapper implements MapperResolver {

    @Override
    public Boolean supports(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

    @Override
    public <T> void map(T instance, Field field, ResultSet resultSet) {
        try {
            Type fieldArgType = getCollectionFieldType(field);

            Class<?> oneToManyFieldClass = (Class<?>) fieldArgType;

            List<Object> oneToManyInstances = new ArrayList<>();

            Object oneToManyInstance = mapResultSetToOneToManyAnnotatedField(resultSet, oneToManyFieldClass);
            oneToManyInstances.add(oneToManyInstance);

            field.setAccessible(true);
            try {
                field.set(instance, oneToManyInstances);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}
