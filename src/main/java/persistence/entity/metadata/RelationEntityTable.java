package persistence.entity.metadata;

import jakarta.persistence.JoinColumn;

import java.lang.reflect.Field;

public class RelationEntityTable {
    private final RelationType relationType;
    private final Class<?> entityClass;
    private Field rootField;

    public RelationEntityTable(RelationType relationType, Class<?> entityClass, Field rootField) {
        this.relationType = relationType;
        this.entityClass = entityClass;
        this.rootField = rootField;
    }

    public RelationType getRelationType() {
        return relationType;
    }


    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getJoinColumn() {
        if (rootField.isAnnotationPresent(JoinColumn.class)) {
            return rootField.getAnnotation(JoinColumn.class).name();
        }
        return null;
    }

    public Field getRootField() {
        return rootField;
    }

    public void setRootField(Field rootField) {
        this.rootField = rootField;
    }
}
