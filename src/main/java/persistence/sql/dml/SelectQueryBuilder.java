package persistence.sql.dml;

import persistence.core.EntityMetadata;
import persistence.dialect.Dialect;
import persistence.dialect.PageQuery;
import persistence.exception.PersistenceException;

import java.util.List;
import java.util.Optional;

public class SelectQueryBuilder {
    private final SelectClauseBuilder selectClauseBuilder;
    private final WhereClauseBuilder whereClauseBuilder;
    private final Dialect dialect;
    private String tableName;
    private Integer limit;
    private int offset;
    private final LeftJoinClauseBuilder leftJoinClauseBuilder;

    public SelectQueryBuilder(final Dialect dialect) {
        this.selectClauseBuilder = SelectClauseBuilder.builder();
        this.whereClauseBuilder = WhereClauseBuilder.builder();
        this.dialect = dialect;
        this.leftJoinClauseBuilder = LeftJoinClauseBuilder.builder();
    }

    public SelectQueryBuilder table(final String tableName) {
        this.tableName = tableName;
        return this;
    }

    public SelectQueryBuilder column(final EntityMetadata<?> entityMetadata) {
        this.selectClauseBuilder.add(entityMetadata);
        return this;
    }

    public SelectQueryBuilder column(final String column) {
        this.selectClauseBuilder.add(column);
        return this;
    }

    public SelectQueryBuilder column(final List<String> columns) {
        this.selectClauseBuilder.addAll(columns);
        return this;
    }

    public SelectQueryBuilder leftJoin(final EntityMetadata<?> entityMetadata) {
        leftJoinClauseBuilder.addJoin(entityMetadata);
        return this;
    }

    public SelectQueryBuilder where(final String column, final String data) {
        this.whereClauseBuilder.and(column, data);
        return this;
    }

    public SelectQueryBuilder where(final List<String> columns, final List<String> data) {
        if (columns.size() != data.size()) {
            throw new PersistenceException("columns size 와 data size 가 같아야 합니다.");
        }
        for (int i = 0; i < columns.size(); i++) {
            where(columns.get(i), data.get(i));
        }
        return this;
    }

    public SelectQueryBuilder limit(final Integer limit) {
        this.limit = limit;
        return this;
    }

    public SelectQueryBuilder offset(final int offset) {
        this.offset = offset;
        return this;
    }

    public String build() {
        if (tableName == null || tableName.isEmpty()) {
            throw new PersistenceException("테이블 이름 없이 select query 를 만들 수 없습니다.");
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("select ")
                .append(selectClauseBuilder.build())
                .append(" from ")
                .append(tableName)
                .append(leftJoinClauseBuilder.build())
                .append(whereClauseBuilder.build());

        final String query = builder.toString();
        return Optional.ofNullable(this.limit)
                .map(limit -> dialect.getPagingStrategy().renderPagingQuery(PageQuery.of(query, this.offset, this.limit)))
                .orElse(query);
    }

}
