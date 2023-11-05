package persistence.entity.attribute;

import persistence.sql.ddl.converter.SqlConverter;

import java.lang.reflect.Field;

public interface GeneralAttribute {
    String prepareDDL(SqlConverter sqlConverter);

    String getColumnName();

    String getFieldName();

    Field getField();
}
