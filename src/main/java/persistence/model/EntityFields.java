package persistence.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityFields {
    private final List<AbstractEntityField> fields = new ArrayList<>();

    public void addField(final AbstractEntityField field) {
        this.fields.add(field);
    }

    public EntityId getIdField() {
        return (EntityId) this.fields.stream().filter(it -> it.getClass().isAssignableFrom(EntityId.class)).findFirst().orElseThrow();
    }

    public List<AbstractEntityField> getFields() {
        return Collections.unmodifiableList(this.fields);
    }
}
