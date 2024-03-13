package database.mapping.column;

import database.dialect.Dialect;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface EntityColumn {

    Field getField();

    Object getValue(Object entity);

    String getColumnName();

    String toColumnDefinition(Dialect dialect);

    boolean isPrimaryKeyField();

    Type getFieldType();
}
