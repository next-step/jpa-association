package persistence.sql.column;

import persistence.sql.dialect.Dialect;

public interface Column {

    String getDefinition(Dialect dialect);

    String getName();

    String getFieldName();
}
