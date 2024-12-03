package persistence.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityUtils {
    public static Long getIdValue(Object entity) {
        Field[] declaredFields = entity.getClass().getDeclaredFields();
        Field idField = Arrays.stream(declaredFields)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow();
        idField.setAccessible(true);
        return (Long) getFieldValue(idField, entity);
    }

    public static Field[] getManagedFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(EntityUtils::isManagedField)
                .toArray(Field[]::new);
    }

    public static Field[] getManagedFieldsExceptId(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(EntityUtils::isManagedField)
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .toArray(Field[]::new);
    }

    private static boolean isManagedField(Field field) {
        if (field.isAnnotationPresent(Transient.class)) {
            return false;
        }
        if (field.isAnnotationPresent(JoinColumn.class)) {
            return false;
        }
//        if (field.isAnnotationPresent(Id.class)
//                && field.isAnnotationPresent(GeneratedValue.class)
//                && GenerationType.IDENTITY.equals(field.getAnnotation(GeneratedValue.class).strategy())) {
//            return false;
//        }
        return true;
    }

    public static Object getFieldValue(Field field, Object entity) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
