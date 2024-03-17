package database.mapping;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityAssociationMetadata {
    private final Class<?> clazz;
    private final List<Field> associationFields;

    public EntityAssociationMetadata(Class<?> clazz) {
        this.clazz = clazz;

        associationFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .collect(Collectors.toList());
    }

    public List<Association> getAssociationRelatedToOtherEntities(List<Class<?>> entities) {
        return entities.stream()
                .filter(this::exceptMe)
                .flatMap(this::getAssociationsBetweenMeAndOther)
                .collect(Collectors.toList());
    }

    private boolean exceptMe(Class<?> entity) {
        return entity != clazz;
    }

    private Stream<Association> getAssociationsBetweenMeAndOther(Class<?> entity) {
        return getAssociationsOf(entity)
                .stream()
                .filter(this::isConnectedToMe);
    }

    private static List<Association> getAssociationsOf(Class<?> entity) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(entity);
        return entityMetadata.getAssociations();
    }

    private boolean isConnectedToMe(Association association) {
        return association.getEntityType() == clazz;
    }

    public List<Association> getAssociations() {
        return associationFields.stream()
                .filter(EntityAssociationMetadata::checkAssociationAnnotation)
                .map(field -> {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    String foreignKeyColumnName = joinColumn.name();
                    Type[] actualTypeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                    Class<?> clazz = (Class<?>) actualTypeArguments[0];
                    String fieldName = field.getName();
                    return new Association(foreignKeyColumnName, clazz, fieldName);
                })
                .collect(Collectors.toList());
    }

    public List<Type> getAssociatedTypes() {
        return associationFields.stream()
                .filter(EntityAssociationMetadata::checkAssociationAnnotation)
                .map(field -> ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0])
                .collect(Collectors.toList());
    }

    private static boolean checkAssociationAnnotation(Field field) {
        return field.isAnnotationPresent(OneToMany.class) && field.isAnnotationPresent(JoinColumn.class);
    }
}
