package persistence.sql.dml.clause.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import persistence.sql.dml.clause.operator.LogicalOperator;
import persistence.sql.dml.clause.predicate.OnPredicate;
import persistence.sql.exception.ClassMappingException;

public class JoinClauseBuilder {

    private static final String JOIN_FORMAT = "JOIN %s ON %s";
    private static final String JOIN_PREDICATE_FORMAT = "%s %s";
    private static final String EMPTY_STRING = "";

    private final String joinTable;
    private final List<JoinOnClause> joinOnClauseList;

    private JoinClauseBuilder(String joinTable, JoinOnClause joinOnClause) {
        if (joinTable == null) {
            throw ClassMappingException.preconditionRequired("Join 테이블명");
        }

        this.joinTable = joinTable;
        this.joinOnClauseList = new ArrayList<>(List.of(joinOnClause));
    }

    public static JoinClauseBuilder builder(String joinTable, OnPredicate onPredicate) {
        return new JoinClauseBuilder(joinTable, new JoinOnClause(null, onPredicate));
    }

    public String build() {
        if (isEmptyJoinOnClause()) {
            return EMPTY_STRING;
        }

        final String predicateJoiningString = joinOnClauseList.stream()
            .map(JoinClauseBuilder::formatPredicate)
            .collect(Collectors.joining(" "));

        return String.format(JOIN_FORMAT, joinTable, predicateJoiningString);
    }

    private static String formatPredicate(JoinOnClause joinOnClause) {
        if (joinOnClause.getOperatorSql().isEmpty()) {
            return joinOnClause.getPredicateCondition();
        }

        return String.format(JOIN_PREDICATE_FORMAT, joinOnClause.getOperatorSql(), joinOnClause.getPredicateCondition());
    }

    public boolean isEmptyJoinOnClause() {
        return this.joinOnClauseList.isEmpty();
    }

    private static class JoinOnClause {

        private final LogicalOperator operator;
        private final OnPredicate predicate;

        public JoinOnClause(LogicalOperator operator, OnPredicate predicate) {
            this.operator = operator;
            this.predicate = predicate;
        }

        public String getOperatorSql() {
            if (operator == null) {
                return EMPTY_STRING;
            }

            return operator.getOperatorSql();
        }

        public String getPredicateCondition() {
            if (predicate == null) {
                return null;
            }

            return predicate.toCondition();
        }
    }
}
