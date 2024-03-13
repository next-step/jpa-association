package persistence.sql.dml;

import persistence.sql.mapping.OneToManyData;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

public class JoinBuilder {
    private final TableData table;
    private final Columns columns;

    public JoinBuilder(TableData table, Columns columns) {
        this.table = table;
        this.columns = columns;
    }

    public String build() {
        StringBuilder query = new StringBuilder();
        OneToManyData association = columns.getOneToMany();
        String joinTableName = association.getJoinTableName();

        query.append(" join ");
        query.append(joinTableName);
        query.append(" on ");
        query.append(table.getName());
        query.append(".");
        query.append(columns.getPkColumnName());
        query.append(" = ");
        query.append(joinTableName);
        query.append(".");
        query.append(association.getJoinColumnName());

        return query.toString();
    }
}
