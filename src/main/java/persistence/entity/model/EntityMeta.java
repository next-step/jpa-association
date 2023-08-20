package persistence.entity.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityMeta {
    private final String tableName;
    private final EntityColumn idColumn;
    private final EntityColumns normalColumns;
    private final OneToManyColumn oneToManyColumn;

    public EntityMeta(String tableName, EntityColumn idColumn, EntityColumns normalColumns, OneToManyColumn oneToManyColumn) {
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.normalColumns = normalColumns;
        this.oneToManyColumn = oneToManyColumn;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumnNames() {
        List<String> columns = new ArrayList<>();
        columns.add(idColumn.getName());
        columns.addAll(normalColumns.getNames());

        return columns.stream()
                .map(it -> String.format("%s.%s", tableName, it))
                .collect(Collectors.toList());
    }

    public Optional<OneToManyColumn> getOneToManyColumn() {
        return Optional.ofNullable(oneToManyColumn);
    }

    public EntityColumn getIdColumn() {
        return idColumn;
    }

    public EntityColumns getNormalColumns() {
        return normalColumns;
    }

    public boolean isNew(Object entity) {
        Object value = idColumn.getValue(entity);
        return value == null || (Long) value == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityMeta that = (EntityMeta) o;
        return Objects.equals(tableName, that.tableName) &&
                Objects.equals(idColumn, that.idColumn) &&
                Objects.equals(normalColumns, that.normalColumns) &&
                Objects.equals(oneToManyColumn, that.oneToManyColumn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, idColumn, normalColumns, oneToManyColumn);
    }
}

