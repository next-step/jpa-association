package persistence.sql.dml.h2;

import persistence.entity.EntityMeta;

import static persistence.sql.util.StringConstant.CHILD_PREFIX;
import static persistence.sql.util.StringConstant.DELIMITER;
import static persistence.sql.util.StringConstant.PARENT_PREFIX;

public final class H2EagerJoinQuery {
    private H2EagerJoinQuery() {}

    public static String build(EntityMeta meta) {
        return new StringBuilder()
                .append("SELECT ")
                .append(buildColumnNames(meta))
                .append(" FROM ")
                .append(buildTableName(meta, PARENT_PREFIX))
                .append(" INNER JOIN ")
                .append(buildTableName(meta.getChildMeta(), CHILD_PREFIX))
                .append(" ON ")
                .append(meta.getFKCondition(PARENT_PREFIX, CHILD_PREFIX))
                .toString();
    }

    private static String buildColumnNames(EntityMeta meta) {
        return meta.joinColumnNames(PARENT_PREFIX)
                + DELIMITER
                + meta.getChildMeta().joinColumnNames(CHILD_PREFIX);
    }

    private static String buildTableName(EntityMeta meta, String prefix) {
        return meta.getTableName() + " AS " + prefix;
    }
}
