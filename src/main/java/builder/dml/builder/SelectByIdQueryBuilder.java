package builder.dml.builder;

import builder.dml.EntityData;

public class SelectByIdQueryBuilder {

    private final static String FIND_BY_ID_QUERY = "SELECT {columnNames} FROM {tableName} WHERE {entityPkName} = {values};";
    private final static String TABLE_NAME = "{tableName}";
    private final static String COLUMN_NAMES = "{columnNames}";
    private final static String VALUES = "{values}";
    private final static String ENTITY_PK_NAME = "{entityPkName}";

    public String buildQuery(EntityData EntityData) {
        return findByIdQuery(EntityData);
    }

    //findAll 쿼리문을 생성한다.
    private String findByIdQuery(EntityData EntityData) {
        return FIND_BY_ID_QUERY.replace(TABLE_NAME, EntityData.getTableName())
                .replace(COLUMN_NAMES, EntityData.getColumnNames())
                .replace(ENTITY_PK_NAME, EntityData.getPkNm())
                .replace(VALUES, String.valueOf(EntityData.wrapString()));
    }

}
