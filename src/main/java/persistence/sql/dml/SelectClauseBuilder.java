package persistence.sql.dml;

import persistence.core.EntityMetadata;
import persistence.exception.PersistenceException;

import java.util.ArrayList;
import java.util.List;

public class SelectClauseBuilder {

    private final List<String> selectColumns;

    private SelectClauseBuilder() {
        this.selectColumns = new ArrayList<>();
    }

    public static SelectClauseBuilder builder() {
        return new SelectClauseBuilder();
    }

    public SelectClauseBuilder add(final EntityMetadata<?> entityMetadata) {
        final List<String> columnClause = entityMetadata.getColumnNamesWithAlias();
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

    public String build() {
        if (selectColumns.isEmpty()) {
            throw new PersistenceException("Data 정보 없이 select query 를 만들 수 없습니다.");
        }
        return String.join(", ", selectColumns);
    }

}
