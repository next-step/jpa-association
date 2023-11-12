package persistence.sql.dml.clause.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import persistence.sql.dml.clause.predicate.OnPredicate;

public class FromClauseBuilder {

    private static final String FROM_FORMAT = "FROM %s";
    private static final String CLAUSE_CONCAT_FORMAT = "%s %s";
    private final StringBuilder fromClauseStringBuilder;
    private final List<JoinClauseBuilder> joinClauseBuilderList;
    private static final String SPACE = " ";

    private FromClauseBuilder(StringBuilder fromClauseStringBuilder) {
        this.fromClauseStringBuilder = fromClauseStringBuilder;
        this.joinClauseBuilderList = new ArrayList<>();
    }

    public static FromClauseBuilder builder(String fromTable) {
        final StringBuilder fromClauseStringBuilder = new StringBuilder();
        fromClauseStringBuilder.append(String.format(FROM_FORMAT, fromTable));
        return new FromClauseBuilder(fromClauseStringBuilder);
    }

    public FromClauseBuilder leftJoin(String joinTable, OnPredicate predicate) {
        this.joinClauseBuilderList.add(JoinClauseBuilder.builder(joinTable, predicate));

        return this;
    }

    public String build() {
        if (joinClauseBuilderList.isEmpty()) {
            return fromClauseStringBuilder.toString();
        }

        return String.format(CLAUSE_CONCAT_FORMAT, fromClauseStringBuilder.toString(),
            joinClauseBuilderList.stream()
                .map(JoinClauseBuilder::build).collect(Collectors.joining(SPACE)));
    }
}
