package builder.dml.builder;

import builder.dml.AliasColumn;
import builder.dml.EntityData;
import jakarta.persistence.FetchType;

import java.util.stream.Collectors;

public class SelectAllQueryBuilder {

    private static final String SELECT_FROM = "SELECT {columnNames} FROM {tableName}";
    private static final String JOIN = "JOIN {joinTableName} ON {basePk} = {joinPK}";
    private static final String TABLE_NAME = "{tableName}";
    private static final String JOIN_TABLE_NAME = "{joinTableName}";
    private static final String BASE_PK = "{basePk}";
    private static final String JOIN_PK = "{joinPK}";
    private static final String COLUMN_NAMES = "{columnNames}";
    private static final String BLANK = " ";
    private static final String SEMI_CLONE = ";";

    public String buildQuery(EntityData entityData) {
        return findAllQuery(entityData);
    }

    //findAll 쿼리문을 생성한다.
    private String findAllQuery(EntityData entityData) {
        String query = SELECT_FROM.replace(TABLE_NAME, entityData.getTableName())
                .replace(COLUMN_NAMES, entityData.getColumnNames());

        if (entityData.checkJoin()) {
            return query + BLANK + joinQuery(entityData) + SEMI_CLONE;
        }

        return query + SEMI_CLONE;
    }

    private String joinQuery(EntityData entityData) {
        return entityData.getJoinEntity().getJoinEntityData().stream()
                .filter(joinEntity -> FetchType.EAGER == joinEntity.getFetchType())
                .map(joinEntity ->
                        JOIN.replace(JOIN_TABLE_NAME, AliasColumn.aliasTable(joinEntity.getTableName()))
                                .replace(BASE_PK, entityData.getPkNm())
                                .replace(JOIN_PK, AliasColumn.aliasColumn(joinEntity.getTableName(), joinEntity.getJoinColumnName())))
                .collect(Collectors.joining(" ")); // 공백으로 조인하여 결과를 반환
    }

}
