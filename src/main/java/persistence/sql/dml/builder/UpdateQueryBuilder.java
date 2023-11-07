package persistence.sql.dml.builder;

import persistence.sql.dml.ColumnValues;
import persistence.sql.meta.EntityMeta;

import java.util.List;

import static persistence.sql.util.StringConstant.COLUMN_JOIN;

public class UpdateQueryBuilder {

    private static final String UPDATE = "UPDATE ";
    private static final String SET = " SET ";

    private final EntityMeta entityMeta;
    private final WhereClauseBuilder whereClauseBuilder;

    private UpdateQueryBuilder(EntityMeta entityMeta) {
        validateEntityAnnotation(entityMeta);
        this.entityMeta = entityMeta;
        this.whereClauseBuilder = WhereClauseBuilder.builder(entityMeta);
    }

    private void validateEntityAnnotation(EntityMeta entityMeta) {
        if (!entityMeta.isEntity()) {
            throw new IllegalArgumentException("Update Query 빌드 대상이 아닙니다.");
        }
    }

    public static UpdateQueryBuilder of(EntityMeta entityMeta) {
        return new UpdateQueryBuilder(entityMeta);
    }

    public String buildUpdateQuery(Object entity) {
        return buildUpdateHeaderQuery(entity) +
                whereClauseBuilder.appendPkClause(entity).build() +
                ";";
    }

    private String buildUpdateHeaderQuery(Object entity) {
        return UPDATE +
                entityMeta.getTableName() +
                SET +
                buildSetClause(entity);
    }

    private String buildSetClause(Object entity) {
        ColumnValues columnValues = ColumnValues.ofFilteredId(entity);
        List<String> valueConditions = columnValues.buildValueConditions();
        return String.join(COLUMN_JOIN, valueConditions);
    }

}
