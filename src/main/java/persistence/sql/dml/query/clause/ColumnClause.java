package persistence.sql.dml.query.clause;

import java.util.List;

import static persistence.sql.constant.SqlConstant.LINE_COMMA;

public class ColumnClause {

    private final List<String> columns;

    public ColumnClause(final List<String> columns) {
        this.columns = columns;
    }

    public String toSql() {
        return String.join(LINE_COMMA.getValue(), columns);
    }

}
