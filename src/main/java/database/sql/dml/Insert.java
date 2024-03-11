package database.sql.dml;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static database.sql.Util.quote;

public class Insert {
    private final String tableName;
    private final String primaryKeyColumnName;
    private final List<String> columnNames;

    private Long id;
    private boolean includeIdField;
    private Map<String, Object> values;

    public Insert(Class<?> clazz) {
        this(EntityMetadataFactory.get(clazz));
    }

    private Insert(EntityMetadata metadata) {
        tableName = metadata.getTableName();
        primaryKeyColumnName = metadata.getPrimaryKeyColumnName();
        columnNames = metadata.getGeneralColumnNames();
        id = null;
        includeIdField = false;
    }

    public Insert id(Long id) {
        this.includeIdField = id != null;
        this.id = id;
        return this;
    }

    public Insert values(Map<String, Object> values) {
        this.values = values;
        return this;
    }

    public String toQueryString() {
        if (values == null) throw new RuntimeException("values are required");

        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnClauses(), valueClauses());
    }

    private List<String> columns(Map<String, Object> valueMap) {
        return columnNames.stream()
                .filter(valueMap::containsKey)
                .collect(Collectors.toList());
    }

    private String columnClauses() {
        List<String> columns = columns(values);
        StringJoiner joiner = new StringJoiner(", ");
        if (includeIdField) {
            joiner.add(primaryKeyColumnName);
        }
        columns.forEach(joiner::add);
        return joiner.toString();
    }

    private String valueClauses() {
        List<String> columns = columns(values);
        StringJoiner joiner = new StringJoiner(", ");
        if (includeIdField) {
            joiner.add(quote(id));
        }
        columns.forEach(key -> joiner.add(quote(values.get(key))));
        return joiner.toString();
    }
}
