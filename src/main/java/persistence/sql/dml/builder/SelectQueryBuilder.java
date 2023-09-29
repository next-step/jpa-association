package persistence.sql.dml.builder;

import persistence.entity.model.EntityColumn;
import persistence.entity.model.EntityMeta;

import static persistence.sql.dml.statement.QueryStatement.selectFrom;
import static persistence.sql.dml.statement.QueryStatement.selectJoin;

public class SelectQueryBuilder {
    public static final SelectQueryBuilder INSTANCE = new SelectQueryBuilder();

    private SelectQueryBuilder() {
    }

    public String findAll(EntityMeta entityMeta) {
        return selectFrom(entityMeta.getTableName(), entityMeta.getColumnNames()).query();
    }

    public String findById(EntityMeta entityMeta, Object id) {
        EntityColumn idColumn = entityMeta.getIdColumn();
        return selectFrom(entityMeta.getTableName(), entityMeta.getColumnNames())
                .where(idColumn.getName(), id.toString())
                .query();
    }

    public String findByIdWithJoin(EntityMeta entityMeta, Object id) {
        EntityColumn idColumn = entityMeta.getIdColumn();
        return selectJoin(entityMeta)
                .where(entityMeta.getTableName() + "." + idColumn.getName(), id.toString())
                .query();
    }

    public String findAllByForeignKey(EntityMeta entityMeta, String foreignKeyName, Object foreignKey) {
        return selectFrom(entityMeta.getTableName(), entityMeta.getColumnNames())
                .where(foreignKeyName, foreignKey.toString())
                .query();
    }
}
