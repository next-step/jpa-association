package persistence.sql.mapping;

public class TableJoin {
    private final String parentName;
    private final String parentTableName;
    private final Table joinedTable;
    private final String joinType;
    private final JoinColumn predicate;

    public TableJoin(String parentName, String parentTableName, Table joinedTable, String joinType, JoinColumn predicate) {
        this.parentName = parentName;
        this.parentTableName = parentTableName;
        this.joinedTable = joinedTable;
        this.joinType = joinType;
        this.predicate = predicate;
    }
}
