package persistence.sql.mapping;

import java.util.*;
import java.util.stream.Collectors;

public class Table {

    private final String name;
    private final Map<String, Column> columns;
    private final PrimaryKey primaryKey;
    private List<TableJoin> tableJoins = new ArrayList<>();
    private List<Table> joinTables = new ArrayList<>();

    public Table(String name) {
        this.name = name;
        this.columns = new LinkedHashMap<>();
        this.primaryKey = new PrimaryKey();
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return this.columns.values().stream().map(Column::clone).collect(Collectors.toList());
    }

    public Column getColumn(final String columnName) {
        return this.columns.get(columnName).clone();
    }

    public PrimaryKey getPrimaryKey() {
        return this.primaryKey;
    }

    public boolean hasPrimaryKey() {
        return this.getPrimaryKey() != null;
    }

    public void addColumn(final Column column) {
        this.columns.put(column.getName(), column);

        if (column.isPk()) {
            primaryKey.addColumn(column);
        }
    }

    public void addColumns(final List<Column> columns) {
        columns.forEach(this::addColumn);
    }

    public void addTableJoins(final List<TableJoin> tableJoins) {
        this.tableJoins.addAll(tableJoins);
    }

    public List<TableJoin> getTableJoins() {
        return Collections.unmodifiableList(this.tableJoins);
    }

    public List<Table> getJoinTables() {
        return Collections.unmodifiableList(this.joinTables);
    }

    public void addJoinTable(final Table joinedTable) {
        this.joinTables.add(joinedTable);
    }
}
