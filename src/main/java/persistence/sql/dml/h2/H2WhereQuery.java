package persistence.sql.dml.h2;

import java.util.Map;
import java.util.stream.Collectors;

import static persistence.sql.util.StringConstant.DELIMITER;

public final class H2WhereQuery {
    private H2WhereQuery() {}

    public static String build(Map<String, Object> condition) {
        return new StringBuilder()
                .append(" WHERE ")
                .append(condition.entrySet().stream().map(
                        entry -> String.format(
                                "%s = %s",
                                entry.getKey(),
                                toString(entry.getValue())
                        )
                ).collect(Collectors.joining(DELIMITER)))
                .toString();
    }

    private static String toString(Object value) {
        return (value instanceof String)
                ? String.format("'%s'", value)
                : value.toString();
    }
}
