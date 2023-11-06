package persistence.entity.loader;

import fixtures.HelloTarget;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import static persistence.entity.loader.MapperResolverHolder.MAPPER_RESOLVERS;

public class OneToManyFieldMapper implements MapperResolver {

    @Override
    public Boolean supports(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

    @Override
    public void map(Object instance, Field field, ResultSet resultSet) {
        FetchType fetchType = field.getAnnotation(OneToMany.class).fetch();

        if (fetchType == FetchType.LAZY) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(HelloTarget.class);
        }

        try {
            Class<?> fieldArgType = getCollectionFieldType(field);
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
