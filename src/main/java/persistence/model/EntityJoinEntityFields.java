package persistence.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityJoinEntityFields {
    private final List<EntityJoinEntityField> joinFields = new ArrayList<>();

    public void addJoinField(final EntityJoinEntityField joinEntityField) {
        this.joinFields.add(joinEntityField);
    }

    public List<EntityJoinEntityField> getJoinFields() {
        return Collections.unmodifiableList(this.joinFields);
    }
}
