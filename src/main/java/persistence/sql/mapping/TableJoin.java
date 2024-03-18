package persistence.sql.mapping;

import java.util.Collections;
import java.util.List;

public class TableJoin {
    private final String parentName;
    private final String parentTableName;
    private final Table joinedTable;
    private final SqlAstJoinType joinType;
    private final JoinColumn predicate;

    public TableJoin(String parentName, String parentTableName, Table joinedTable, SqlAstJoinType joinType, JoinColumn predicate) {
        this.parentName = parentName;
        this.parentTableName = parentTableName;
        this.joinedTable = joinedTable;
        this.joinType = joinType;
        this.predicate = predicate;
    }

    public String getJoinType() {
        return this.joinType.toString();
    }

    public String getTableName() {
        return this.joinedTable.getName();
    }

    public List<Column> getJoinedTableColumns() {
        return Collections.unmodifiableList(this.joinedTable.getColumns());
    }
}
