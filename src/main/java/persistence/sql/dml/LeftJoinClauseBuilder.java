package persistence.sql.dml;

import java.util.LinkedHashMap;
import java.util.Map;

public class LeftJoinClauseBuilder {

    private final Map<String, OnClauseBuilder> joinData;
    private final SelectQueryBuilder owner;

    private LeftJoinClauseBuilder(final SelectQueryBuilder selectQueryBuilder) {
        this.owner = selectQueryBuilder;
        this.joinData = new LinkedHashMap<>();
    }

    public static LeftJoinClauseBuilder builder(final SelectQueryBuilder selectQueryBuilder) {
        return new LeftJoinClauseBuilder(selectQueryBuilder);
    }

    public OnClauseBuilder leftJoin(final String tableName) {
        final OnClauseBuilder onClauseBuilder = OnClauseBuilder.builder(owner);
        joinData.put(tableName, onClauseBuilder);
        return onClauseBuilder;
    }

    public String build() {
        if (joinData.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        joinData.forEach((joinTableName, onClauseBuilder) ->
                builder.append(" left join ")
                        .append(joinTableName)
                        .append(onClauseBuilder.build())
        );

        return builder.toString();
    }

}
