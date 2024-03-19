package persistence.sql.dml;

import persistence.sql.mapping.Associations;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.OneToManyData;
import persistence.sql.mapping.TableData;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SelectQueryBuilder {
    private final TableData table;
    private final Columns columns;
    private final Associations associations;

    public SelectQueryBuilder(TableData table, Columns columns, Associations associations) {
        this.table = table;
        this.columns = columns;
        this.associations = associations;
    }

    public String build(WhereBuilder whereBuilder) {
        StringBuilder query = new StringBuilder();
        query.append("select ");
        query.append(selectClause(columns));
        if(associations.isNotEmpty()){
            query.append(getJoinTableSelect(associations));
        }

        query.append(" from ");
        query.append(table.getName());

        if(associations.isNotEmpty()) {
            JoinBuilder joinBuilder = new JoinBuilder(table, columns, associations);
            query.append(joinBuilder.build());
        }

        if(whereBuilder.isEmpty()) {
            return query.toString();
        }

        query.append(whereBuilder.toClause());

        return query.toString();
    }

    private String getJoinTableSelect(Associations associations) {
        StringBuilder stringBuilder = new StringBuilder();
        for(OneToManyData association : associations.getEagerAssociations()) {
            stringBuilder.append(", ");
            String line = selectClause(Columns.createColumns(association.getReferenceEntityClazz()));
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }

    private String selectClause(Columns columns) {
        ArrayList<String> names = new ArrayList<String>();
        names.add(columns.getPkColumnName());
        names.addAll(columns.getNamesWithTableName());
        return String.join(", ", names);
    }
}
