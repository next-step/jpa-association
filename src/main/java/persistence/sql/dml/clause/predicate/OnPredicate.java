package persistence.sql.dml.clause.predicate;

import persistence.sql.dml.clause.operator.ComparisonOperator;

public class OnPredicate {

    public static final String PREDICATE_FORMAT = "%s %s %s";

    private final String columnName;
    private final ComparisonOperator onOperator;
    private final Object onValue;

    private OnPredicate(String columnName, Object onValue, ComparisonOperator onOperator) {
        this.columnName = columnName;
        this.onValue = onValue;
        this.onOperator = onOperator;
    }

    public static OnPredicate of(String columnName, Object onValue, ComparisonOperator onOperator) {
        return new OnPredicate(columnName, onValue, onOperator);
    }

    public String toCondition() {
        return String.format(PREDICATE_FORMAT, columnName, onOperator.getOperatorSql(), onValue);
    }
}
