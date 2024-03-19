package persistence.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityFields {
    private final List<EntityField> fields = new ArrayList<>();
    private final List<EntityJoinEntityField> joinFields = new ArrayList<>();

    public void addField(final EntityField field) {
        this.fields.add(field);
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
