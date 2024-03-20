package database.dialect;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Dialect {
    String convertToSqlTypeDefinition(Class<?> type, Integer columnLength);

    Object getFieldValueFromResultSet(ResultSet resultSet, int columnIndex, int sqlType) throws SQLException;

    String autoIncrementDefinition();

    String primaryKeyDefinition();

    String nullableDefinition(boolean nullable);
}
