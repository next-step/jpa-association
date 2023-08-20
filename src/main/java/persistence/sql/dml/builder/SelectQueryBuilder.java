package persistence.sql.dml.builder;

import persistence.entity.model.EntityColumn;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.OneToManyColumn;

import java.util.Optional;

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
        Optional<OneToManyColumn> oneToManyColumn = entityMeta.getOneToManyColumn();
        EntityColumn idColumn = entityMeta.getIdColumn();

        if (oneToManyColumn.isPresent()) {
            return selectJoin(entityMeta, oneToManyColumn.get())
                    .where(entityMeta.getTableName() + "." + idColumn.getName(), id.toString())
                    .query();
        }

        return selectFrom(entityMeta.getTableName(), entityMeta.getColumnNames())
                .where(idColumn.getName(), id.toString())
                .query();
    }
}
