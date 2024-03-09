package persistence.sql.column;

import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;

public interface Column {

    String getDefinition(Dialect dialect);

    String getName();

    String getFieldName();

    Field getField();
}
