package persistence.meta;

import static persistence.validator.AnnotationValidator.isNotPresent;

import jakarta.persistence.Transient;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record SchemaMeta(Class<?> clazz,
                         List<ColumnMeta> columnMetas,
                         List<ColumnValueMeta> columnValueMetas,
                         TableMeta tableMeta,
                         PrimaryKeyConstraint primaryKeyConstraint) {

    public SchemaMeta(Class<?> clazz) {
        this(
                clazz,
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> isNotPresent(field, Transient.class))
                        .map(ColumnMeta::new)
                        .toList(),
                Collections.emptyList(),
                new TableMeta(clazz),
                PrimaryKeyConstraint.from(clazz)
        );
    }

    public SchemaMeta(Object instance) {
        this(
                instance.getClass(),
                Arrays.stream(instance.getClass().getDeclaredFields())
                        .filter(field -> isNotPresent(field, Transient.class))
                        .map(ColumnMeta::new)
                        .toList(),
                Arrays.stream(instance.getClass().getDeclaredFields())
                        .filter(field -> isNotPresent(field, Transient.class))
                        .map(field -> ColumnValueMeta.of(field, instance))
                        .toList(),
                new TableMeta(instance.getClass()),
                PrimaryKeyConstraint.from(instance.getClass())
        );
    }

    private boolean hasRelation() {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(ColumnMeta::new)
                .map(ColumnMeta::relationMeta)
                .anyMatch(RelationMeta::hasRelation);
    }

    public boolean hasNotRelation() {
        return !hasRelation();
    }

    public String tableName() {
        return tableMeta.name();
    }

    public String primaryKeyColumnName() {
        return primaryKeyConstraint.column().name();
    }

    public List<String> columnNames() {
        return columnMetas.stream()
                .map(ColumnMeta::name)
                .toList();
    }

    public List<String> columnNamesWithoutPrimaryKey() {
        return columnMetas.stream()
                .filter(ColumnMeta::isNotPrimaryKey)
                .map(ColumnMeta::name)
                .toList();
    }

    public List<Object> columnValuesWithoutPrimaryKey() {
        return columnValueMetas.stream()
                .filter(ColumnValueMeta::isNotPrimaryKey)
                .map(ColumnValueMeta::value)
                .toList();
    }

    public List<Object> columnValuesMatchWith(List<ColumnMeta> columnMetas, Object instance) {
        return columnMetas.stream()
                .map(columnMeta -> ColumnValueMeta.of(columnMeta.field(), instance))
                .map(ColumnValueMeta::value)
                .toList();
    }

    public List<ColumnMeta> columnMetasHasRelation() {
        return columnMetas.stream()
                .filter(ColumnMeta::isNotPrimaryKey)
                .filter(columnMeta -> columnMeta.relationMeta().hasRelation())
                .toList();
    }

    public List<ColumnMeta> columnMetasHasNotRelation() {
        return columnMetas.stream()
                .filter(ColumnMeta::isNotPrimaryKey)
                .filter(columnMeta -> columnMeta.relationMeta().hasNotRelation())
                .toList();
    }

    public List<String> columnNamesWithoutRelation() {
        return columnMetas.stream()
                .filter(columnMeta -> columnMeta.relationMeta().hasNotRelation())
                .map(ColumnMeta::name)
                .toList();
    }

}
