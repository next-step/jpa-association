package database.mapping;

import database.dialect.MySQLDialect;
import jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

// TODO: 테스트 추가
public class MultiRowMapper<T> implements RowMapper<RowMap<T>> {
    private final Class<T> clazz;
    private final MySQLDialect dialect;

    public MultiRowMapper(Class<T> clazz, MySQLDialect dialect) {
        this.clazz = clazz;
        this.dialect = dialect;
    }

    @Override
    public RowMap<T> mapRow(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        RowMap<T> rowMap = new RowMap<>(clazz);
        for (int i = 1; i < resultSetMetaData.getColumnCount() + 1; i++) {
            rowMap.add(getTableName(resultSetMetaData, i),
                       getColumnName(resultSetMetaData, i),
                       getValue(resultSetMetaData, resultSet, i));
        }
        return rowMap;
    }

    private String getColumnName(ResultSetMetaData metaData, int i) throws SQLException {
        return metaData.getColumnName(i);
    }

    private String getTableName(ResultSetMetaData metaData, int i) throws SQLException {
        return metaData.getTableName(i);
    }

    private Object getValue(ResultSetMetaData metaData, ResultSet resultSet, int i) throws SQLException {
        String columnName = getColumnName(metaData, i);
        return dialect.getFieldValueFromResultSet(resultSet, columnName, metaData.getColumnType(i));
    }
}
