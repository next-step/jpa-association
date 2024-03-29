package persistence.sql.dml;

import persistence.sql.mapping.Column;
import persistence.sql.mapping.Value;

public class Where {

    private final String tableName;
    private final Column column;
    private final Value value;
    private LogicalOperator logicalOperator;
    private final WhereOperator whereOperator;

    public Where(final Column column, final Value value, final LogicalOperator logicalOperator, final WhereOperator whereOperator) {
        this(column.getTableName(), column, value, logicalOperator, whereOperator);
    }

    public Where(final String tableName, final Column column, final Value value, final LogicalOperator logicalOperator, final WhereOperator whereOperator) {
        this.tableName = tableName;
        this.column = column;
        this.value = value;
        this.logicalOperator = logicalOperator;
        this.whereOperator = whereOperator;
    }

    public String getColumnTableName() {
        return this.tableName;
    }

    public String getColumnName() {
        return this.column.getName();
    }

    public Value getColumnValue() {
        return this.value;
    }

    public String getLogicalOperator() {
        return this.logicalOperator.getOperator();
    }

    public String getWhereOperator(final String valueClause) {
        return this.whereOperator.operatorClause(valueClause);
    }

    public void changeLogicalOperator(final LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }
}
