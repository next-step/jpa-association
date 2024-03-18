package persistence.model;

import jakarta.persistence.*;
import persistence.sql.QueryException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class EntityMetaData {

    private final String entityName;
    private final Class<?> entityClass;
    private final Map<String, Field> fields = new LinkedHashMap<>();
    private final List<EntityJoinEntityField> joinFields = new ArrayList<>();

    private static final Class<? extends Annotation>[] joinAnnotations = new Class[]{OneToOne.class, OneToMany.class, ManyToOne.class, ManyToMany.class};

    public EntityMetaData(final Class<?> entityClass) {
        this.entityName = entityClass.getName();
        this.entityClass = entityClass;
        extractFields(entityClass);
    }

    private void extractFields(final Class<?> entityClass) {
        Arrays.stream(entityClass.getDeclaredFields())
                .filter(this::isColumnField)
                .forEach(field -> {
                    if (isJoinColumnField(field)) {
                        joinFields.add(new EntityJoinEntityField(field));
                        return;
                    }
                    this.fields.put(field.getName(), field);
                });
    }

    private boolean isColumnField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    private boolean isJoinColumnField(final Field field) {
        return Arrays.stream(joinAnnotations)
                .anyMatch(field::isAnnotationPresent);
    }

    public String getEntityName() {
        return this.entityName;
    }

    public Class<?> getEntityType() {
        return this.entityClass;
    }

    public List<Field> getFields() {
        return new ArrayList<>(this.fields.values());
    }

    public List<EntityJoinEntityField> getJoinFields() {
        return Collections.unmodifiableList(this.joinFields);
    }

    public Map<String, Object> extractValues(final Object entity) {
        if (!entity.getClass().equals(entityClass)) {
            throw new MetaDataModelMappingException("not equal class type - meta data type: " + entityName + ", parameter type: " + entity.getClass().getName());
        }

        final Map<String, Object> values = new HashMap<>();

        fields.forEach((fieldName, field) -> {
            try {
                field.setAccessible(true);
                values.put(fieldName, field.get(entity));
            } catch (IllegalAccessException e) {
                throw new QueryException("can't get field " + field.getName() + " at " + entity.getClass().getName());
            } finally {
                field.setAccessible(false);
            }
        });

        return values;
    }
}
