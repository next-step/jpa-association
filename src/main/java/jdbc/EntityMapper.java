package jdbc;

import builder.dml.EntityData;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import proxy.LazyProxyBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityMapper {

    private final static String FAILED_GET_COLUMN = "컬럼 데이터를 가져오는데 실패했습니다.";
    private final static String FAILED_ACCESS_FIELD = "필드에 접근을 실패했습니다.";
    private final static String FAILED_CREATE_INSTANCE = "인스턴스를 생성하는데 실패하였습니다.";

    private static int joinIndex = 0;
    private static EntityData entityData;

    @SuppressWarnings("unchecked")
    public static <T> T mapRow(ResultSet rs, EntityData entityData) {
        EntityMapper.entityData = entityData;
        if (entityData.getJoinEntity().checkJoin()) {
            return (T) confirmAnnotationSetColumnContainJoinEntity(rs, entityData.getClazz());
        }
        return (T) confirmAnnotationSetColumnMainEntity(rs, entityData.getClazz());
    }

    public static <T> T mapRow(ResultSet rs, Class<T> entityClass) {
        entityData = EntityData.createEntityData(entityClass);
        if (entityData.getJoinEntity().checkJoin()) {
            return confirmAnnotationSetColumnContainJoinEntity(rs, entityClass);
        }
        return confirmAnnotationSetColumnMainEntity(rs, entityClass);
    }

    private static <T> T confirmAnnotationSetColumnContainJoinEntity(ResultSet rs, Class<T> entityClass) {
        T entityInstance = confirmAnnotationSetColumnMainEntity(rs, entityClass);

        Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .forEach(field -> setJoinEntity(field, rs, entityInstance));

        return entityInstance;
    }

    private static <T> void setJoinEntity(Field field, ResultSet rs, T entityInstance) {
        if (entityData.getJoinEntity().checkFetchEager()) {
            setListValueInField(field, entityInstance, fetchRelatedEntities(field, rs));
            return;
        }
        setListValueInField(field, entityInstance, fetchRelatedEntitiesProxy(field));
    }


    @SuppressWarnings("unchecked")
    private static <T> List<T> fetchRelatedEntitiesProxy(Field field) {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Class<?> joinEntityClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];

        return (List<T>) new LazyProxyBuilder<>().createProxy(entityData.getJoinEntity().findJoinEntityData(joinEntityClass));
    }


    @SuppressWarnings("unchecked")
    private static <T> List<T> fetchRelatedEntities(Field field, ResultSet rs) {
        List<T> relatedEntities = new ArrayList<>();

        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Class<?> joinEntityClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];

        try {
            do {
                T relatedEntity = (T) createAndPopulateEntity(rs, joinEntityClass);
                relatedEntities.add(relatedEntity);
            }
            while (rs.next());
        } catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(FAILED_CREATE_INSTANCE);
        }

        return relatedEntities;
    }

    private static <T> T createAndPopulateEntity(ResultSet rs, Class<T> entityClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLException {
        T entityInstance = entityClass.getDeclaredConstructor().newInstance();
        int index = joinIndex;

        for (Field field : entityClass.getDeclaredFields()) {
            setValueInField(rs, field, entityInstance, index);
            index++;
        }

        return entityInstance;
    }

    private static <T> T confirmAnnotationSetColumnMainEntity(ResultSet rs, Class<T> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        T entityInstance = createNewInstance(entityClass);
        int index = 1;
        for (Field field : fields) {
            if (noCheckAnnotation(field)) continue;
            setValueInField(rs, field, entityInstance, index);
            index++;
        }
        joinIndex = index;
        return entityInstance;
    }

    private static <T> T createNewInstance(Class<T> entityClass) {
        try {
            return entityClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(FAILED_CREATE_INSTANCE);
        }
    }

    private static <T> void setListValueInField(Field field, T entityInstance, List<T> listObject) {
        try {
            field.setAccessible(true);
            field.set(entityInstance, listObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(FAILED_ACCESS_FIELD);
        }
    }

    private static <T> void setValueInField(ResultSet rs, Field field, T entityInstance, int index) {
        try {
            field.setAccessible(true);
            Object value = rs.getObject(index);
            field.set(entityInstance, value);
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_GET_COLUMN);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(FAILED_ACCESS_FIELD);
        }
    }

    private static boolean noCheckAnnotation(Field field) {
        return field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(OneToMany.class);
    }
}
