package database.sql.ddl;

import database.dialect.Dialect;

public class QueryBuilder {
    private static final QueryBuilder INSTANCE = new QueryBuilder();

    private QueryBuilder() {
    }

    public static QueryBuilder getInstance() {
        return INSTANCE;
    }

    public String buildCreateQuery(Class<?> clazz, Dialect dialect) {
        Create create = new Create(clazz, dialect);
        return create.buildQuery();
    }

    public String buildDeleteQuery(Class<?> clazz) {
        Drop drop = new Drop(clazz);
        return drop.buildQuery();
    }
}
