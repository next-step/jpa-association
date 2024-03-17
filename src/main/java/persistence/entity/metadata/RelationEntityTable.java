package persistence.entity.metadata;

import java.lang.reflect.Field;

public class RelationEntityTable {

    private final RelationType relationType;
    private final Class<?> entity;
    private Field rootField;
    private final String joinColumnName;

    public RelationEntityTable(RelationType relationType, Class<?> entity, Field rootField, String joinColumnName) {
        this.relationType = relationType;
        this.entity = entity;
        this.rootField = rootField;
        this.joinColumnName = joinColumnName;
    }

    public RelationType getRelationType() {
        return relationType;
    }


    public Class<?> getEntity() {
        return entity;
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }

    public Field getRootField() {
        return rootField;
    }

    public void setRootField(Field rootField) {
        this.rootField = rootField;
    }
}
