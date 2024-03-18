package persistence.sql.dml;

import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SelectQueryBuilder {
    private final TableData table;
    private final Columns columns;

    public SelectQueryBuilder(TableData table, Columns columns) {
        this.table = table;
        this.columns = columns;
    }

    private String buildTemplate(WhereBuilder whereBuilder, JoinBuilder joinBuilder) {
        StringBuilder query = new StringBuilder();
        query.append("select ");
        query.append(selectClause(columns));
        String associationSelects = getJoinTableSelect();
        if(!associationSelects.isEmpty()){
            query.append(", ");
            query.append(associationSelects);
        }

        query.append(" from ");
        query.append(table.getName());

        if(joinBuilder != null) {
            query.append(joinBuilder.build());
        }

        if(whereBuilder.isEmpty()) {
            return query.toString();
        }

        query.append(whereBuilder.toClause());

        return query.toString();
    }

    public String build(WhereBuilder whereBuilder) {
        return buildTemplate(whereBuilder, null);
    }

    public String buildWithJoin(WhereBuilder whereBuilder, JoinBuilder joinBuilder) {
        return buildTemplate(whereBuilder, joinBuilder);
    }

    private String getJoinTableSelect() {
        return columns.getEagerAssociations().stream().map(association ->
            selectClause(Columns.createColumns(association.getReferenceEntityClazz()))
        ).collect(Collectors.joining(", "));
    }

    private String selectClause(Columns columns) {
        ArrayList<String> names = new ArrayList<String>();
        names.add(columns.getPkColumnName());
        names.addAll(columns.getNamesWithTableName());
        return String.join(", ", names);
    }
}
