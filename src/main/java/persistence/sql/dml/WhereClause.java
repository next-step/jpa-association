package persistence.sql.dml;

public class WhereClause {
    private final StringBuilder conditions = new StringBuilder();

    private void and(String condition) {
        if (conditions.length() > 0) {
            conditions.append(" AND ");
        }
        conditions.append(condition);
    }

    public void and(String columnName, String value) {
        and(String.format("%s = '%s'", columnName, value));
    }

    public void and(String alias, String columnName, String value) {
        and(String.format("%s.%s = '%s'", alias, columnName, value));
    }

    public String prepareDML() {
        if (conditions.length() == 0) {
            return "";
        }
        return " WHERE " + conditions;
    }
}
