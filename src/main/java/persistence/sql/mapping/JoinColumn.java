package persistence.sql.mapping;

import persistence.sql.dml.ComparisonOperator;

public class JoinColumn {

    private final String leftHandExpression;
    private final String rightHandExpression;
    private final ComparisonOperator.Comparisons operator;

    public JoinColumn(final String leftHandExpression, final String rightHandExpression, final ComparisonOperator.Comparisons operator) {
        this.leftHandExpression = leftHandExpression;
        this.rightHandExpression = rightHandExpression;
        this.operator = operator;
    }

    public String getLeftHandExpression() {
        return this.leftHandExpression;
    }

    public String getRightHandExpression() {
        return this.rightHandExpression;
    }

    public String getOperator() {
        return this.operator.getOperator();
    }
}
