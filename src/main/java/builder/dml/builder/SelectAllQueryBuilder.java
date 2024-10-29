package builder.dml.builder;

import builder.dml.EntityData;

public class SelectAllQueryBuilder {

    private final static String FIND_ALL_QUERY = "SELECT {columnNames} FROM {tableName};";
    private final static String TABLE_NAME = "{tableName}";
    private final static String COLUMN_NAMES = "{columnNames}";

    public String buildQuery(EntityData EntityData) {
        return findAllQuery(EntityData);
    }

    //findAll 쿼리문을 생성한다.
    private String findAllQuery(EntityData EntityData) {
        return FIND_ALL_QUERY.replace(TABLE_NAME, EntityData.getTableName())
                .replace(COLUMN_NAMES, EntityData.getColumnNames());
    }

}
