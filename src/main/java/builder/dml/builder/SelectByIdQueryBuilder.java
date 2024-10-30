package builder.dml.builder;

import builder.dml.AliasColumn;
import builder.dml.EntityData;
import jakarta.persistence.FetchType;

import java.util.stream.Collectors;

public class SelectByIdQueryBuilder {

    private final static String SELECT_FROM = "SELECT {columnNames} FROM {tableName}";
    private static final String WHERE = "WHERE {entityPkName} = {values}";
    private static final String JOIN = "JOIN {joinTableName} ON {basePk} = {joinPK}";
    private final static String TABLE_NAME = "{tableName}";
    private static final String JOIN_TABLE_NAME = "{joinTableName}";
    private static final String BASE_PK = "{basePk}";
    private static final String JOIN_PK = "{joinPK}";
    private final static String COLUMN_NAMES = "{columnNames}";
    private final static String VALUES = "{values}";
    private final static String ENTITY_PK_NAME = "{entityPkName}";
    private static final String BLANK = " ";
    private static final String SEMI_CLONE = ";";

    public String buildQuery(EntityData entityData) {
        return findByIdQuery(entityData);
    }

    private String findByIdQuery(EntityData entityData) {
        String selectFrom = SELECT_FROM.replace(TABLE_NAME, entityData.getTableName())
                .replace(COLUMN_NAMES, entityData.getColumnNames());

        String where = WHERE.replace(ENTITY_PK_NAME, entityData.getPkNm())
                .replace(VALUES, String.valueOf(entityData.wrapString()));

        if (entityData.checkJoin()) {
            return selectFrom + BLANK +
                    join(entityData) + BLANK +
                    where + SEMI_CLONE;
        }

        return selectFrom + BLANK +
                where + SEMI_CLONE;
    }

    private String join(EntityData entityData) {
        return entityData.getJoinEntity().getJoinEntityData().stream()
                .filter(joinEntity -> FetchType.EAGER == joinEntity.getFetchType())
                .map(joinEntity ->
                        JOIN.replace(JOIN_TABLE_NAME, AliasColumn.aliasTable(joinEntity.getTableName()))
                                .replace(BASE_PK, entityData.getPkNm())
                                .replace(JOIN_PK, AliasColumn.aliasColumn(joinEntity.getTableName(),joinEntity.getJoinColumnName())))
                .collect(Collectors.joining(" ")); // 공백으로 조인하여 결과를 반환
    }

}
