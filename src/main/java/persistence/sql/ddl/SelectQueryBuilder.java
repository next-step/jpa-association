package persistence.sql.ddl;

import persistence.EntityMeta;

public abstract class SelectQueryBuilder {

    public String findAll(String tableName) {
        return String.format("select * from %s", tableName);
    }

    public String findById(String tableName, String columnName, String columnValue) {
        return String.format("select * from %s where %s=%s", tableName, columnName, columnValue);
    }

    public String findByIdByJoin(EntityMeta entityMeta, Object id) {
        return new StringBuilder()
                .append(String.format("select %s.* from %s", entityMeta.tableName(), entityMeta.tableName()))
                .append(String.format(" join %s", entityMeta.joinTableName()))
                .append(String.format(" on %s=%s", entityMeta.joinRootColumn(), entityMeta.joinJoinColumn()))
                .append(String.format(" where %s=%s", entityMeta.uniqueColumn(entityMeta.tableName()), id))
                .toString();
    }
}
