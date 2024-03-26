package persistence.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityFields {
    private final List<AbstractEntityField> fields = new ArrayList<>();

    public void addFields(final List<AbstractEntityField> entityFields) {
        this.fields.addAll(entityFields);
    }

    public EntityId getIdField() {
        return (EntityId) this.fields.stream().filter(it -> it.getClass().isAssignableFrom(EntityId.class)).findFirst().orElseThrow();
    }

    public List<AbstractEntityField> getFields() {
        return Collections.unmodifiableList(this.fields);
    }

    public List<EntityJoinField> getJoinFields() {
        return this.fields.stream().filter(it -> it.getClass().isAssignableFrom(EntityJoinField.class))
                .map(field -> (EntityJoinField) field)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<AbstractEntityField> getColumns() {
        return this.fields.stream().filter(it -> it.getClass().isAssignableFrom(EntityId.class) || it.getClass().isAssignableFrom(EntityField.class))
                .collect(Collectors.toUnmodifiableList());
    }
}
