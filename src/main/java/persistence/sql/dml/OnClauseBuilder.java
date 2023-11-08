package persistence.sql.dml;

import java.util.LinkedHashMap;
import java.util.Map;

public class OnClauseBuilder {

    private final SelectQueryBuilder owner;
    private final Map<String, String> onData;

    private OnClauseBuilder(final SelectQueryBuilder selectQueryBuilder) {
        this.owner = selectQueryBuilder;
        this.onData = new LinkedHashMap<>();
    }

    public static OnClauseBuilder builder(final SelectQueryBuilder selectQueryBuilder) {
        return new OnClauseBuilder(selectQueryBuilder);
    }

    public SelectQueryBuilder on(final String leftColumn, final String rightColumn) {
        onData.put(leftColumn, rightColumn);
        return owner;
    }

    String build() {
        if (onData.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        builder.append(" on ");
        onData.forEach((leftColumn, rightColumn) ->
                builder.append(leftColumn)
                        .append(" = ")
                        .append(rightColumn)
        );
        return builder.toString();
    }

}
