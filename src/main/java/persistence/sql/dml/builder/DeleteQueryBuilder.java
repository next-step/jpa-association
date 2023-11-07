package persistence.sql.dml.builder;

import persistence.sql.meta.EntityMeta;

public class DeleteQueryBuilder {

    private static final String DELETE = "DELETE";
    private static final String FROM = " FROM ";

    private final EntityMeta entityMeta;
    private final WhereClauseBuilder whereClauseBuilder;

    private DeleteQueryBuilder(EntityMeta entityMeta) {
        validateEntityAnnotation(entityMeta);
        this.entityMeta = entityMeta;
        this.whereClauseBuilder = WhereClauseBuilder.builder(entityMeta);
    }

    private void validateEntityAnnotation(EntityMeta entityMeta) {
        if (!entityMeta.isEntity()) {
            throw new IllegalArgumentException("Delete Query 빌드 대상이 아닙니다.");
        }
    }

    public static DeleteQueryBuilder of(EntityMeta entityMeta) {
        return new DeleteQueryBuilder(entityMeta);
    }

    public String buildDeleteAllQuery() {
        return getDeleteHeaderQuery() +
                ";";
    }

    public String buildDeleteByPkQuery(Object pkObject) {
        return getDeleteHeaderQuery() +
                whereClauseBuilder.buildPkClause(pkObject) +
                ";";
    }

    public String buildDeleteQuery(Object entity) {
        return getDeleteHeaderQuery() +
                whereClauseBuilder.appendPkClause(entity).build() +
                ";";
    }

    private String getDeleteHeaderQuery() {
        return DELETE +
                FROM +
                entityMeta.getTableName();
    }
}
