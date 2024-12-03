package persistence.sql.component;

public class JoinConditionBuilder {
    private JoinType joinType;
    private TableInfo tableInfo;
    private ColumnInfo sourceColumnInfo;
    private ColumnInfo targetColumnInfo;

    public JoinConditionBuilder joinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    public JoinConditionBuilder tableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
        return this;
    }

    public JoinConditionBuilder sourceColumnInfo(ColumnInfo sourceColumnInfo) {
        this.sourceColumnInfo = sourceColumnInfo;
        return this;
    }

    public JoinConditionBuilder targetColumnInfo(ColumnInfo targetColumnInfo) {
        this.targetColumnInfo = targetColumnInfo;
        return this;
    }

    public JoinCondition build() {
        return new JoinCondition(joinType, tableInfo, sourceColumnInfo, targetColumnInfo);
    }
}
