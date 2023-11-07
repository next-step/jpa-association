package persistence.sql.dml.builder;

import persistence.sql.meta.ColumnMetas;
import persistence.sql.meta.EntityMeta;
import persistence.sql.util.StringConstant;

import java.util.Arrays;
import java.util.List;

public class SelectQueryBuilder {

    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";

    private final EntityMeta entityMeta;
    private final WhereClauseBuilder whereClauseBuilder;
    private final JoinClauseBuilder joinClauseBuilder;

    private SelectQueryBuilder(EntityMeta entityMeta) {
        validateEntityAnnotation(entityMeta);
        this.entityMeta = entityMeta;
        this.whereClauseBuilder = WhereClauseBuilder.builder(entityMeta);
        this.joinClauseBuilder = JoinClauseBuilder.of(entityMeta);
    }

    private void validateEntityAnnotation(EntityMeta entityMeta) {
        if (!entityMeta.isEntity()) {
            throw new IllegalArgumentException("Select Query 빌드 대상이 아닙니다.");
        }
    }

    public static SelectQueryBuilder of(EntityMeta entityMeta) {
        return new SelectQueryBuilder(entityMeta);
    }

    public static List<String> extractSelectColumns(String query) {
        String headerQuery = query.split(FROM)[0];
        headerQuery = headerQuery.replace(SELECT, StringConstant.EMPTY_STRING);
        return Arrays.asList(headerQuery.split(StringConstant.COLUMN_JOIN));
    }

    public String buildSelectAllQuery() {
        return getSelectHeaderQuery() +
                ";";
    }

    public String buildSelectByPkQuery(Object pkObject) {
        return getSelectHeaderQuery() +
                whereClauseBuilder.buildPkClause(pkObject) +
                ";";
    }

    private String getSelectHeaderQuery() {
        ColumnMetas columnMetas = entityMeta.getColumnMetas();
        ColumnMetas exceptTransient = columnMetas.exceptTransient();
        ColumnMetas exceptJoin = exceptTransient.exceptJoin();
        return SELECT +
                exceptJoin.getColumnsClause() +
                FROM +
                entityMeta.getTableName();
    }

    public String buildSelectWithJoinByPkQuery(Object pkObject) {
        return getSelectHeaderQueryWithJoin() +
                joinClauseBuilder.build() +
                whereClauseBuilder.buildPkClauseWithAlias(pkObject) +
                ";";
    }

    private String getSelectHeaderQueryWithJoin() {
        ColumnMetas columnMetas = entityMeta.getColumnMetas();
        ColumnMetas exceptTransient = columnMetas.exceptTransient();
        return SELECT +
                exceptTransient.getJoinColumnsClause(entityMeta.getTableName()) +
                FROM +
                entityMeta.getTableName();
    }

}
