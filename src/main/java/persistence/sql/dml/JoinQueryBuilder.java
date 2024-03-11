package persistence.sql.dml;

import java.util.Map;
import java.util.Set;
import static persistence.sql.constant.SqlConstant.DOT;
import static persistence.sql.constant.SqlConstant.EMPTY;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class JoinQueryBuilder {

    private static final String LEFT_JOIN_DEFINITION = "LEFT JOIN %s ON %s = %s";

    private JoinQueryBuilder() {
    }

    private static class Holder {
        static final JoinQueryBuilder INSTANCE = new JoinQueryBuilder();
    }

    public static JoinQueryBuilder getInstance() {
        return JoinQueryBuilder.Holder.INSTANCE;
    }

    public String generateLeftJoinQuery(Table root, Table relationTable) {
        Set<Map.Entry<Table, Column>> relationColumn =  Table.getRelationColumns(relationTable);
        return relationColumn.stream()
            .filter(entry -> entry.getKey().equals(root))
            .map(entry -> String.format(LEFT_JOIN_DEFINITION, relationTable.getTableName(),
                DOT.concat(root.getTableName(), root.getIdColumnName()),
                DOT.concat(relationTable.getTableName(), entry.getValue().getColumnName())))
            .findFirst().orElse(EMPTY.getValue());
    }
}
