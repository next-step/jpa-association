package persistence.sql.dml.builder;

import persistence.entity.model.EntityColumn;
import persistence.entity.model.EntityMeta;
import persistence.sql.dml.statement.QueryStatement;

public class UpdateQueryBuilder {
    public static final UpdateQueryBuilder INSTANCE = new UpdateQueryBuilder();

    public String update(EntityMeta entityMeta, Object entity) {
        EntityColumn idColumn = entityMeta.getIdColumn();

        return QueryStatement.update(entityMeta.getTableName())
                .set(entityMeta.getNormalColumns().getEntityColumns(), entity)
                .where(idColumn.getName(), idColumn.getValue(entity).toString())
                .query();
    }
}
