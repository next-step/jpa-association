package database.dialect;

import persistence.sql.meta.DataType;

public interface Dialect {
    String convertClassForDialect(DataType dataType);

    String getCreateQueryTemplate();

    String getDropQueryTemplate();
}
