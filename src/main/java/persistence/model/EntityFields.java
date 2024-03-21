package persistence.model;

import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityFields {
    private EntityField idField;
    private final List<EntityField> fields = new ArrayList<>();
    private final List<EntityJoinEntityField> joinFields = new ArrayList<>();

    public void addField(final EntityField field) {
        if (field.getField().isAnnotationPresent(Id.class)) {
            this.idField = field;
        }
        this.fields.add(field);
    }

    public EntityField getIdField() {
        return this.idField;
    }

    public void addJoinField(final EntityJoinEntityField joinEntityField) {
        this.joinFields.add(joinEntityField);
    }

    public List<EntityField> getFields() {
        return Collections.unmodifiableList(this.fields);
    }

    public List<EntityJoinEntityField> getJoinFields() {
        return Collections.unmodifiableList(this.joinFields);
    }
}
