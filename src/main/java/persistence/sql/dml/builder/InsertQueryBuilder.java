package persistence.sql.dml.builder;

import persistence.entity.model.EntityColumns;
import persistence.entity.model.EntityMeta;

public class InsertQueryBuilder {
    private static final String INSERT_QUERY_FORMAT = "insert into %s (%s) values (%s)";
    public static final InsertQueryBuilder INSTANCE = new InsertQueryBuilder();

    private InsertQueryBuilder() {
    }

    public String insert(EntityMeta entityMeta, Object object) {
        String tableName = entityMeta.getTableName();
        EntityColumns normalColumns = entityMeta.getNormalColumns();
        String columnNames = String.join(", ", normalColumns.getNames());
        String columnValues = normalColumns.getValuesQuery(object);

        return String.format(INSERT_QUERY_FORMAT, tableName, columnNames, columnValues);
    }
}
