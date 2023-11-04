package persistence.sql.meta;

import persistence.sql.util.StringConstant;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnMetas implements Iterable<ColumnMeta> {

    private final List<ColumnMeta> values;

    private ColumnMetas(List<ColumnMeta> values) {
        this.values = values;
    }

    public static ColumnMetas of(Field[] fields) {
        return new ColumnMetas(Arrays.stream(fields)
                .map(ColumnMeta::of)
                .collect(Collectors.toList()));
    }

    public ColumnMetas exceptTransient() {
        List<ColumnMeta> exceptTransient = values.stream()
                .filter(columnMeta -> !columnMeta.isTransient())
                .collect(Collectors.toList());
        return new ColumnMetas(exceptTransient);
    }

    public ColumnMetas exceptJoin() {
        List<ColumnMeta> exceptTransient = values.stream()
                .filter(columnMeta -> !columnMeta.isJoinColumn())
                .collect(Collectors.toList());
        return new ColumnMetas(exceptTransient);
    }

    public ColumnMetas idColumns() {
        List<ColumnMeta> exceptTransient = values.stream()
                .filter(ColumnMeta::isId)
                .collect(Collectors.toList());
        return new ColumnMetas(exceptTransient);
    }

    public String getColumnsClause() {
        return String.join(StringConstant.COLUMN_JOIN, getColumnNames());
    }

    private List<String> getColumnNames() {
        return values.stream()
                .map(ColumnMeta::getColumnName)
                .collect(Collectors.toList());
    }

    public String getJoinColumnsClause(String masterEntityName) {
        List<String> columnNames = exceptJoin().getColumnNamesWithAlias(masterEntityName);
        List<String> joinColumnNames = values.stream()
                .filter(ColumnMeta::isJoinColumn)
                .map(joinColumn -> {
                    EntityMeta joinTableEntityMeta = joinColumn.getJoinTableEntityMeta();
                    ColumnMetas columnMetas = joinTableEntityMeta.getColumnMetas();
                    return columnMetas.getJoinColumnsClause(joinTableEntityMeta.getTableName());
                })
                .collect(Collectors.toList());
        columnNames.addAll(joinColumnNames);
        return String.join(StringConstant.COLUMN_JOIN, columnNames);
    }

    private List<String> getColumnNamesWithAlias(String alias) {
        return values.stream()
                .map(columnMeta -> alias + StringConstant.DOT + columnMeta.getColumnName())
                .collect(Collectors.toList());
    }

    public boolean hasAutoGenId() {
        return values.stream()
                .anyMatch(ColumnMeta::isGenerationTypeIdentity);
    }

    public boolean hasJoinEntity() {
        return values.stream()
                .anyMatch(ColumnMeta::isJoinColumn);
    }

    @Override
    public Iterator<ColumnMeta> iterator() {
        return values.iterator();
    }

}
