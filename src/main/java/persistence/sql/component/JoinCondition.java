package persistence.sql.component;

public class JoinCondition {
    private JoinType joinType;
    private TableInfo tableInfo;
    private ColumnInfo sourceColumnInfo;
    private ColumnInfo targetColumnInfo;

    public JoinType getJoinType() {
        return joinType;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public ColumnInfo getSourceColumnInfo() {
        return sourceColumnInfo;
    }

    public ColumnInfo getTargetColumnInfo() {
        return targetColumnInfo;
    }

    public JoinCondition(JoinType joinType, TableInfo tableInfo, ColumnInfo onConditionColumn1, ColumnInfo onConditionColumn2) {
        this.joinType = joinType;
        this.tableInfo = tableInfo;
        this.sourceColumnInfo = onConditionColumn1;
        this.targetColumnInfo = onConditionColumn2;
    }
}
