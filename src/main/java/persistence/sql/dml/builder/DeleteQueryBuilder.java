package persistence.sql.dml.builder;

import persistence.entity.model.EntityColumn;
import persistence.entity.model.EntityMeta;
import persistence.sql.dml.statement.QueryStatement;

public class DeleteQueryBuilder {
    public static final DeleteQueryBuilder INSTANCE = new DeleteQueryBuilder();

    private DeleteQueryBuilder() {
    }

    public String delete(EntityMeta entityMeta, Object entity) {
        EntityColumn idColumn = entityMeta.getIdColumn();
        return QueryStatement.delete(entityMeta.getTableName())
                .where(idColumn.getName(), idColumn.getValue(entity).toString())
                .query();
    }
}
