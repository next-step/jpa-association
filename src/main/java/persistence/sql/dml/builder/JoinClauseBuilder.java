package persistence.sql.dml.builder;

import persistence.sql.meta.ColumnMeta;
import persistence.sql.meta.ColumnMetas;
import persistence.sql.meta.EntityMeta;

import static persistence.sql.util.StringConstant.*;

public class JoinClauseBuilder {

    private static final String JOIN = " JOIN ";
    private static final String ON = " ON ";
    private final EntityMeta entityMeta;

    private JoinClauseBuilder(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    public static JoinClauseBuilder of(EntityMeta entityMeta) {
        return new JoinClauseBuilder(entityMeta);
    }

    public String build() {
        ColumnMetas columnMetas = entityMeta.getColumnMetas();
        if (!columnMetas.hasJoinEntity()) {
            return EMPTY_STRING;
        }
        return build(columnMetas);
    }

    private String build(ColumnMetas columnMetas) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ColumnMeta columnMeta : columnMetas) {
            stringBuilder.append(build(columnMeta));
        }
        return stringBuilder.toString();
    }

    private StringBuilder build(ColumnMeta columnMeta) {
        return new StringBuilder()
                .append(JOIN)
                .append(columnMeta.getJoinTableName())
                .append(ON)
                .append(buildColumnClause(columnMeta));
    }

    private String buildColumnClause(ColumnMeta joinColumnMeta) {
        return new StringBuilder()
                .append(entityMeta.getTableName())
                .append(DOT)
                .append(entityMeta.getPkColumnName())
                .append(EQUAL)
                .append(joinColumnMeta.getJoinTableName())
                .append(DOT)
                .append(joinColumnMeta.getJoinColumnName())
                .toString();
    }

}
