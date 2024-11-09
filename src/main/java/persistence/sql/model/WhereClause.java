package persistence.sql.model;

public class WhereClause {

    private final String columnName;
    private final Operator operator;
    private final Object value;
    private final String tableAlias;

    public WhereClause(String columnName, Operator operator, Object value, String tableAlias) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
        this.tableAlias = tableAlias;
    }

    public String makeWhereQuery() {
        return String.format("WHERE %s.%s %s %s", tableAlias, columnName, operator.getValue(), value);
    }

}
