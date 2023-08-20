package persistence.entity.model;

import java.util.List;
import java.util.stream.Collectors;

public class EntityColumns {
    private final List<EntityColumn> entityColumns;

    public EntityColumns(List<EntityColumn> entityColumns) {
        this.entityColumns = entityColumns;
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns;
    }

    public List<String> getNames() {
        return entityColumns.stream()
                .map(EntityColumn::getName)
                .collect(Collectors.toList());
    }

    public List<Object> getValues(Object entity) {
        return entityColumns.stream()
                .map(it -> it.getValue(entity))
                .collect(Collectors.toList());
    }

    public String getValuesQuery(Object entity) {
        return entityColumns.stream()
                .map(it -> it.getValueQuery(entity))
                .collect(Collectors.joining(", "));
    }
}
