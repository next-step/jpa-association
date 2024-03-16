package database.mapping.column;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface EntityColumn {

    Field getField();

    Object getValue(Object entity);

    String getColumnName();

    boolean isPrimaryKeyField();

    Type getFieldType();

    Class<?> getType();

    Integer getColumnLength();
}
