package persistence.sql.dml.querybuilder;

import jakarta.persistence.OneToMany;
import persistence.sql.ddl.clause.table.TableClause;

import java.util.Arrays;

public class SelectQueryBuilder {
    public static final String SELECT_ALL_QUERY = "SELECT * FROM %s";
    public static final String SELECT_BY_ID_QUERY = "SELECT * FROM %s WHERE %s = %d";
    private final TableClause tableClause;

    public SelectQueryBuilder(Class<?> clazz) {
        this.tableClause = new TableClause(clazz);
    }

    public String getFindAllQuery() {
        if (this.tableClause.hasJoinedEntity()) {
            return String.format(SELECT_ALL_QUERY, tableClause.name()) + this.tableClause.createJoinQuery();
        }
        return String.format(SELECT_ALL_QUERY, tableClause.name());
    }
    public String getFindById(Long id) {
        return String.format(SELECT_BY_ID_QUERY, tableClause.name(), tableClause.primaryKeyName(), id);
    }
}
