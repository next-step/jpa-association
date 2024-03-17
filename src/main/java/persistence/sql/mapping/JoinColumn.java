package persistence.sql.mapping;

import persistence.sql.dml.QueryOperator;

public class JoinColumn {

    private final Column leftHandExpression;
    private final Column rightHandExpression;
    private final QueryOperator operator;

    public JoinColumn(Column leftHandExpression, Column rightHandExpression, QueryOperator operator) {
        this.leftHandExpression = leftHandExpression;
        this.rightHandExpression = rightHandExpression;
        this.operator = operator;
    }
}
