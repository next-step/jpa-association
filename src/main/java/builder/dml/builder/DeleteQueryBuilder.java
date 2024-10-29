package builder.dml.builder;

import builder.dml.EntityData;

public class DeleteQueryBuilder {

    private final static String DELETE_BY_ID_QUERY = "DELETE FROM {tableName} WHERE {entityPkName} = {values};";
    private final static String TABLE_NAME = "{tableName}";
    private final static String VALUES = "{values}";
    private final static String ENTITY_PK_NAME = "{entityPkName}";

    public String buildQuery(EntityData EntityData) {
        return deleteByIdQuery(EntityData);
    }

    //delete 쿼리문을 생성한다.
    private String deleteByIdQuery(EntityData EntityData) {
        return DELETE_BY_ID_QUERY.replace(TABLE_NAME, EntityData.getTableName())
                .replace(ENTITY_PK_NAME, EntityData.getPkNm())
                .replace(VALUES, String.valueOf(EntityData.wrapString()));
    }

}
