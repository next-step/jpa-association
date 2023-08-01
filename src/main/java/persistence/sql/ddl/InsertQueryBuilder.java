package persistence.sql.ddl;

import persistence.EntityMeta;

public abstract class InsertQueryBuilder {

    protected InsertQueryBuilder() {
    }

    public String createInsertBuild(Object object) {
        ColumnMap columnMap = ColumnMap.of(object);
        EntityMeta entityMeta = EntityMeta.of(object.getClass());
        return String.format("insert into %s (%s) values (%s)", entityMeta.tableName(), columnMap.names(), columnMap.values());
    }
}
