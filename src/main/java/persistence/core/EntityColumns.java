package persistence.core;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.exception.ColumnNotExistException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class EntityColumns implements Iterable<EntityColumn> {
    private final List<EntityColumn> columns;

    public EntityColumns(final Class<?> clazz, final String tableName) {
        this.columns = generateColumns(clazz,tableName);
    }

    public EntityColumns(final List<EntityColumn> entityColumns) {
        this.columns = entityColumns;
    }

    private List<EntityColumn> generateColumns(final Class<?> clazz, final String tableName) {
        this.validate(clazz);
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map(field -> EntityColumn.from(field, tableName))
                .collect(Collectors.toUnmodifiableList());
    }

    private void validate(final Class<?> clazz) {
        if (this.isIdFieldAbsent(clazz)) {
            throw new ColumnNotExistException("Id 필드가 존재하지 않습니다.");
        }
    }

    private boolean isIdFieldAbsent(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .noneMatch(field -> field.isAnnotationPresent(Id.class));
    }

    @Override
    public Iterator<EntityColumn> iterator() {
        return this.columns.iterator();
    }

    public Stream<EntityColumn> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public int size() {
        return this.columns.size();
    }

    public EntityIdColumn getId() {
        return this.columns.stream()
                .filter(EntityColumn::isId)
                .findFirst()
                .map(EntityIdColumn.class::cast)
                .orElseThrow(() -> new ColumnNotExistException("Id 컬럼이 존재하지 않습니다."));
    }

    public List<String> getNames() {
        return this.columns.stream()
                .map(EntityColumn::getName)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<String> getFieldNames() {
        return this.columns.stream()
                .map(EntityColumn::getFieldName)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<EntityOneToManyColumn> getOneToManyColumns() {
        return this.columns.stream()
                .filter(EntityColumn::isOneToMany)
                .map(EntityOneToManyColumn.class::cast)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<EntityFieldColumn> getFieldColumns() {
        return this.columns.stream()
                .filter(EntityColumn::isField)
                .map(EntityFieldColumn.class::cast)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final EntityColumns that = (EntityColumns) object;
        return Objects.equals(columns, that.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns);
    }
}
