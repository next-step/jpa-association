package persistence.inspector;

import jakarta.persistence.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityInfoExtractor {

    private static List<Class<? extends Annotation>> RELATIONSHIP_ANNOTATIONS =
            Arrays.asList(OneToMany.class, ManyToOne.class, OneToOne.class, ManyToMany.class);

    public static String getColumnName(Field field) {
        return EntityFieldInspector.getColumnName(field);
    }

    public static String getTableName(Class<?> clazz) {
        if (ClsssMetadataInspector.hasAnnotation(clazz, Table.class) && (!clazz.getAnnotation(Table.class).name().isBlank())) {

                return clazz.getAnnotation(Table.class).name();
        }

        return clazz.getSimpleName().toLowerCase();
    }

    public static boolean isPrimaryKey(Field id) {
        return EntityFieldInspector.hasAnnotation(id, Id.class);
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        return ClsssMetadataInspector.getAllFields(clazz);
    }

    public static List<Field> getColumns(Class<?> clazz) {
        return ClsssMetadataInspector.getAllFields(clazz)
                .stream()
                .filter(EntityFieldInspector::isPersistable)
                    .collect(Collectors.toList());
    }

    public static boolean isRelationshipField(Field field) {
        return RELATIONSHIP_ANNOTATIONS.stream()
                .anyMatch(annotation -> field.isAnnotationPresent(annotation));
    }

    public static Class<?> getFieldClassType(Field field) {
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            return (Class<?>) actualTypeArguments[0];
        }
        return field.getType();
    }


}
