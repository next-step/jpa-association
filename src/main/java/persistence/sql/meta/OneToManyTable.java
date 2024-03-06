package persistence.sql.meta;

import jakarta.persistence.FetchType;

import java.util.List;

public class OneToManyTable implements AssociationTable {
    private final Table table;
    private final String joinColumnName;
    private final FetchType fetchType;

    public OneToManyTable(Table table, String joinColumnName, FetchType fetchType) {
        this.table = table;
        this.joinColumnName = joinColumnName;
        this.fetchType = fetchType;
    }

    @Override
    public List<Column> getColumns() {
        return table.getColumns();
    }

    @Override
    public String getName() {
        return table.getName();
    }

    @Override
    public String getJoinColumn() {
        return joinColumnName;
    }
}
