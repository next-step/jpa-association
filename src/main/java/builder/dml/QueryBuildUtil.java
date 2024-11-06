package builder.dml;

import java.util.stream.Collectors;

public class QueryBuildUtil {

    private static final String UNDER_BAR = "_";
    private static final String DOT = ".";
    private static final String BLANK = " ";
    private static final String COMMA = ", ";

    public static String getAlias(String table) {
        return table + UNDER_BAR;
    }

    public static String getContainAliasTableName(String tableName, String alias) {
        return tableName + BLANK + alias;
    }

    public static String getContainAliasColumnName(String columnName, String alias) {
        return alias + DOT + columnName;
    }

    public static String getTableName(EntityData entityData) {
        if (entityData.checkJoinAndEager()) {
            return entityData.getTableName() + BLANK + entityData.getAlias();
        }
        return entityData.getTableName();
    }

    public static String getTableName(JoinEntityData joinEntityData) {
        return joinEntityData.getTableName();
    }

    public static String getColumnNames(EntityData entityData) {
        if (entityData.checkJoinAndEager()) {
            String baseColumnNames = entityData.getEntityColumn().getColumns().stream()
                    .map(columnData -> getContainAliasColumnName(columnData.getColumnName(), entityData.getAlias())) // aliasColumn 메서드 사용
                    .collect(Collectors.joining(COMMA));

            JoinEntityData joinEntityData = entityData.getJoinEntity().getJoinEntityData().getFirst();

            String joinColumn = joinEntityData.getJoinColumnData().getColumns().stream()
                    .map(joinColumnData -> getContainAliasColumnName(joinColumnData.getColumnName(), joinEntityData.getAlias()))
                    .collect(Collectors.joining(COMMA));

            return baseColumnNames + COMMA + joinColumn;
        }

        return entityData.getEntityColumn().getColumnNames();
    }

    public static String getColumnNames(JoinEntityData joinEntityData) {
        return joinEntityData.getJoinColumnData().getColumnNames();
    }

}
