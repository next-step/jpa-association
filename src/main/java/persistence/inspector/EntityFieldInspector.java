package persistence.inspector;

import jakarta.persistence.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class EntityFieldInspector {

    private static List<Class<? extends Annotation>> ENTITY_RELATION_ANNOTATIONS =
            Arrays.asList(OneToMany.class, ManyToOne.class, OneToOne.class, ManyToMany.class);
    private static List<Class<? extends Annotation>> NOT_MANAGED_ANNOTATION =
            Arrays.asList(Transient.class, OneToMany.class, ManyToOne.class, OneToOne.class, ManyToMany.class);

    public static boolean isPersistable(Field field) {

        return !NOT_MANAGED_ANNOTATION.stream()
                .anyMatch(annotation -> field.isAnnotationPresent(annotation));
    }

    public static boolean isRelationship(Field field) {
        return ENTITY_RELATION_ANNOTATIONS.stream()
                .anyMatch(annotation -> field.isAnnotationPresent(annotation));
    }
    public static String getColumnName(Field field) {
        return field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isBlank() ?
                field.getAnnotation(Column.class).name() : field.getName();
    }

    public static EntityColumnType getColumnType(Field field) {
        return EntityColumnType.get(field.getType());
    }

    public static boolean isNullable(Field field) {
        return !field.isAnnotationPresent(Column.class) || field.getAnnotation(Column.class).nullable();
    }

    public static boolean isPrimaryKey(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    public static boolean isAutoIncrement(Field field) {
        return field.isAnnotationPresent(GeneratedValue.class);
    }

    public static boolean hasAnnotation(Field field, Class<? extends Annotation> annotation) {
        return field.isAnnotationPresent(annotation);
    }

    public static GenerationType getGenerationType(Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {

            return field.getAnnotation(GeneratedValue.class).strategy();
        }

        return null;
    }

    public static int getLength(Field field) {
        if (field.isAnnotationPresent(Column.class)) {

            return field.getAnnotation(Column.class).length();
        }

        return 0;
    }

}
