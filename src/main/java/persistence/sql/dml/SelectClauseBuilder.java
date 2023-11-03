package persistence.sql.dml;

import jakarta.persistence.FetchType;
import persistence.core.EntityColumn;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityOneToManyColumn;
import persistence.exception.PersistenceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SelectClauseBuilder {

    private final List<String> selectColumns;

    private SelectClauseBuilder() {
        this.selectColumns = new ArrayList<>();
    }

    public static SelectClauseBuilder builder() {
        return new SelectClauseBuilder();
    }

    public SelectClauseBuilder add(final EntityMetadata<?> entityMetadata) {
        final List<String> columnClause = entityMetadata.getColumns().stream()
                .map(entityColumn -> getColumnNames(entityMetadata, entityColumn))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        selectColumns.addAll(columnClause);
        return this;
    }

    public SelectClauseBuilder add(final String column) {
        selectColumns.add(column);
        return this;
    }

    public SelectClauseBuilder addAll(final List<String> columns) {
        selectColumns.addAll(columns);
        return this;
    }

    private List<String> getColumnNames(final EntityMetadata<?> entityMetadata, final EntityColumn entityColumn) {
        if (entityColumn.isOneToMany()) {
            final EntityOneToManyColumn oneToManyColumn = (EntityOneToManyColumn) entityColumn;
            if (oneToManyColumn.getFetchType().equals(FetchType.LAZY)) {
                return Collections.emptyList();
            }
            final EntityMetadata<?> oneToManyEntityMetadata = getLeftJoiningEntityMetadata(oneToManyColumn);
            return oneToManyEntityMetadata.getColumnNamesWithAlias();
        } else {
            return List.of(entityColumn.getNameWithAlias());
        }
    }


    private static EntityMetadata<?> getLeftJoiningEntityMetadata(final EntityOneToManyColumn entityOneToManyColumn) {
        return EntityMetadataProvider.getInstance().getEntityMetadata(entityOneToManyColumn.getJoinColumnType());
    }

    public String build() {
        if (selectColumns.isEmpty()) {
            throw new PersistenceException("Data 정보 없이 select query 를 만들 수 없습니다.");
        }
        return String.join(", ", selectColumns);
    }

}
