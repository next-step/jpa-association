package database.sql.dml;

import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import database.sql.dml.part.ValueMap;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static database.sql.Util.quote;

public class Insert {
    private final String tableName;
    private final List<GeneralEntityColumn> generalColumns;
    private final PrimaryKeyEntityColumn primaryKey;

    private Long id;
    private boolean includeIdField;
    private ValueMap values;

    public Insert(String tableName, PrimaryKeyEntityColumn primaryKey, List<GeneralEntityColumn> generalColumns) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.generalColumns = generalColumns;

        id = null;
        includeIdField = false;
    }

    public Insert id(Long id) {
        this.includeIdField = id != null;
        this.id = id;
        return this;
    }

    public Insert values(ValueMap valueMap) {
        this.values = valueMap;
        return this;
    }

    public Insert valuesFromEntity(Object entity) {
        return this.values(ValueMap.fromEntity(entity, generalColumns));
    }

    public String toQueryString() {
        if (values == null) throw new RuntimeException("values are required");

        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnClauses(), valueClauses());
    }

    private List<String> columns(ValueMap valueMap) {
        return generalColumns.stream()
                .map(GeneralEntityColumn::getColumnName)
                .filter(valueMap::containsKey)
                .collect(Collectors.toList());
    }

    private String columnClauses() {
        StringJoiner joiner = new StringJoiner(", ");
        if (includeIdField) {
            joiner.add(primaryKey.getColumnName());
        }
        columns(values).forEach(joiner::add);
        return joiner.toString();
    }

    private String valueClauses() {
        StringJoiner joiner = new StringJoiner(", ");
        if (includeIdField) {
            joiner.add(quote(id));
        }
        columns(values).forEach(key -> joiner.add(quote(values.get(key))));
        return joiner.toString();
    }
}
