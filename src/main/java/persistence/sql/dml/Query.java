package persistence.sql.dml;

import persistence.entity.EntityMeta;
import persistence.sql.common.instance.Values;

public final class Query {

    private static final Query INSTANCE = new Query();

    private final SelectQuery selectQuery;
    private final InsertQuery insertQuery;
    private final UpdateQuery updateQuery;
    private final DeleteQuery deleteQuery;

    private Query() {
        this.selectQuery = new SelectQuery();
        this.insertQuery = new InsertQuery();
        this.updateQuery = new UpdateQuery();
        this.deleteQuery = new DeleteQuery();
    }

    public static Query getInstance() {
        return INSTANCE;
    }

    public String select(EntityMeta entityMeta, Object arg) {
        return INSTANCE.selectQuery.get(entityMeta, arg);
    }

    public String selectJoin(EntityMeta entityMeta, Object arg) {
        return INSTANCE.selectQuery.getJoin(entityMeta, arg);
    }

    public String selectAll(EntityMeta entityMeta) {
        return INSTANCE.selectQuery.getAll(entityMeta);
    }

    public String insert(EntityMeta entityMeta, Values values) {
        return INSTANCE.insertQuery.get(entityMeta, values);
    }

    public String update(EntityMeta entityMeta, Values values, Object args) {
        return INSTANCE.updateQuery.get(entityMeta, values, args);
    }

    public String delete(EntityMeta entityMeta, Object arg) {
        return INSTANCE.deleteQuery.get(entityMeta, arg);
    }
}
