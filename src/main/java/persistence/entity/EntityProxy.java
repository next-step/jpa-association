package persistence.entity;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

public class EntityProxy {
    private EntityProxy() {
    }

    public static Object createProxy(Object entity, QueryBuilder queryBuilder) {
        Field childField = joinField(entity.getClass());
        childField.setAccessible(true);
        ParameterizedType genericType = (ParameterizedType) childField.getGenericType();
        Class<?> childType = (Class<?>) genericType.getActualTypeArguments()[0];

        Enhancer enhancer = getEnhancer(entity, childType, queryBuilder);

        try {
            childField.set(entity, enhancer.create());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return entity;
    }


    private static Enhancer getEnhancer(Object entity, Class<?> childType, QueryBuilder queryBuilder) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(List.class);
        enhancer.setCallback((LazyLoader) () -> {
            try {
                CustomJoinTable customJoinTable = CustomJoinTable.of(entity.getClass());
                Field idField = unique(entity.getClass().getDeclaredFields());
                idField.setAccessible(true);
                return queryBuilder.findAllBy(customJoinTable.joinTable(), customJoinTable.joinColumn(), idField.get(entity), childType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        return enhancer;
    }

    private static Field joinField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .findFirst()
                .orElseThrow();
    }

    private static Field unique(Field[] field) {
        return Arrays.stream(field)
                .filter(it -> it.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
