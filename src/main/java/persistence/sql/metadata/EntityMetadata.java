package persistence.sql.metadata;

import jakarta.persistence.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntityMetadata {
    private final Table table;

    private final List<Column> columns;

    private final Column idColumn;

    private final Column associatedColumn;

    public EntityMetadata(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Entity 클래스가 아닙니다.");
        }
        this.table = new Table(clazz);
        this.columns = Arrays.stream(clazz.getDeclaredFields())
                .map(Column::new)
                .collect(Collectors.toList());
        this.idColumn = columns.stream()
                .filter(Column::isPrimaryKey)
                .findAny()
                .orElseThrow(NoSuchFieldError::new);
        this.associatedColumn = columns.stream()
                .filter(Column::hasAssociation)
                .findAny()
                .orElse(null);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String getIdName() {
        return idColumn.getName();
    }

    public String getTableName() {
        return table.getName();
    }

    public Column getAssociatedColumn() {
        return associatedColumn;
    }

    public boolean hasAssociation() {
        return Objects.nonNull(associatedColumn);
    }
}
