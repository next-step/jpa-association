package persistence.sql.dml;

import persistence.sql.mapping.Associations;
import persistence.sql.mapping.OneToManyData;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

public class JoinBuilder {
    private final TableData table;
    private final Columns columns;
    private final Associations associations;

    public JoinBuilder(TableData table, Columns columns, Associations associations) {
        this.table = table;
        this.columns = columns;
        this.associations = associations;
    }

    public String build() {
        StringBuilder query = new StringBuilder();
        OneToManyData association = associations.getEagerAssociations().get(0);
        String joinTableName = association.getJoinTableName();

        query.append(" join ");
        query.append(joinTableName);
        query.append(" on ");
        query.append(columns.getPkColumnName());
        query.append(" = ");
        query.append(association.getJoinColumnName());

        return query.toString();
    }
}
