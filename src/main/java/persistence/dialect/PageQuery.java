package persistence.dialect;

import persistence.exception.PersistenceException;

public class PageQuery {

    private final String query;
    private final int offset;
    private final int limit;

    private PageQuery(final String query, final int offset, final int limit) {
        validate(offset, limit);
        this.query = query;
        this.offset = offset;
        this.limit = limit;
    }

    public static PageQuery of(final String query, final int offset, final int limit) {
        return new PageQuery(query, offset, limit);
    }

    private void validate(final int offset, final int limit) {
        validateOffset(offset);
        validateLimit(limit);
    }

    private void validateLimit(final int limit) {
        if (limit < 0) {
            throw new PersistenceException("limit 은 0보다 작을 수 없습니다.");
        }
    }

    private void validateOffset(final int offset) {
        if (offset < 0) {
            throw new PersistenceException("offset 은 0보다 작을 수 없습니다.");
        }
    }

    public String getQuery() {
        return this.query;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getMaxResult() {
        return this.offset + this.limit;
    }

    public boolean shouldRenderOffset() {
        return this.offset > 0;
    }
}
