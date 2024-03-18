package persistence.sql.mapping;

import persistence.sql.dml.ComparisonOperator;

public class JoinColumn {

    private final Column leftHandExpression;
    private final Column rightHandExpression;
    private final ComparisonOperator operator;

    public JoinColumn(Column leftHandExpression, Column rightHandExpression, ComparisonOperator operator) {
        this.leftHandExpression = leftHandExpression;
        this.rightHandExpression = rightHandExpression;
        this.operator = operator;
    }
}
