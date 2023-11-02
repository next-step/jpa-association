package persistence.sql.dml;

import jakarta.persistence.FetchType;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityOneToManyColumn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeftJoinClauseBuilder {
    private static final String DOT = ".";

    private final Map<String, String> joinData;

    private LeftJoinClauseBuilder() {
        this.joinData = new LinkedHashMap<>();
    }

    public static LeftJoinClauseBuilder builder() {
        return new LeftJoinClauseBuilder();
    }

    public LeftJoinClauseBuilder addJoin(final EntityMetadata<?> entityMetadata) {
        final Map<String, String> joinOnClause = entityMetadata.getOneToManyColumns().stream()
                .filter(entityOneToManyColumn -> entityOneToManyColumn.getFetchType().equals(FetchType.EAGER))
                .collect(Collectors.toMap(
                        this::getJoiningTableName,
                        entityOneToManyColumn -> getJoiningCondition(entityMetadata, entityOneToManyColumn)
                ));
        joinData.putAll(joinOnClause);
        return this;
    }


    private String getJoiningCondition(final EntityMetadata<?> entityMetadata, final EntityOneToManyColumn entityOneToManyColumn) {
        final EntityMetadata<?> leftJoiningEntityMetadata = getLeftJoiningEntityMetadata(entityOneToManyColumn);
        return combineTableNameWithColumn(entityMetadata.getTableName(), entityMetadata.getIdColumnName()) + " = " + combineTableNameWithColumn(leftJoiningEntityMetadata.getTableName(), entityOneToManyColumn.getName());
    }

    private static EntityMetadata<?> getLeftJoiningEntityMetadata(final EntityOneToManyColumn entityOneToManyColumn) {
        return EntityMetadataProvider.getInstance().getEntityMetadata(entityOneToManyColumn.getJoinColumnType());
    }

    private String getJoiningTableName(final EntityOneToManyColumn entityOneToManyColumn) {
        final EntityMetadata<?> leftJoiningEntityMetadata = getLeftJoiningEntityMetadata(entityOneToManyColumn);
        return leftJoiningEntityMetadata.getTableName();
    }

    private String combineTableNameWithColumn(final String tableName, final String columnName) {
        return tableName + DOT + columnName;
    }

    public String build() {
        if (joinData.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        builder.append(" left join ");
        final List<String> columns = new ArrayList<>(joinData.keySet());
        for (final String column : columns) {
            builder.append(column)
                    .append(" on ")
                    .append(joinData.get(column));
        }
        return builder.toString();
    }

}
