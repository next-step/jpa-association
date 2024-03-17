package database.dialect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import static java.sql.Types.*;

public class MySQLDialect implements Dialect {
    private static final SqlTypes sqlTypes = new SqlTypes();
    private final Map<Class<?>, Integer> javaTypeMap;

    public static MySQLDialect INSTANCE = new MySQLDialect();

    private MySQLDialect() {
        javaTypeMap = new HashMap<>();
        register(Long.class, Types.BIGINT);
        register(String.class, Types.VARCHAR);
        register(Integer.class, Types.INTEGER);
    }

    public static MySQLDialect getInstance() {
        return INSTANCE;
    }

    private void register(Class<?> javaTypeName, Integer sqlType) {
        javaTypeMap.put(javaTypeName, sqlType);
    }

    // TODO: 첫번째 인자 타입 Type 으로 변경?
    @Override
    public String convertToSqlTypeDefinition(Class<?> type, Integer columnLength) {
        String sqlType = javaTypeToSqlType(type);
        // TODO: 여기는 필요할 때 개선하겠습니다.
        if (sqlType.equals("VARCHAR")) {
            return sqlType + "(" + columnLength + ")";
        }
        return sqlType;
    }

    private String javaTypeToSqlType(Class<?> type) {
        Integer javaType = javaTypeMap.get(type);
        String sqlTypeName = sqlTypes.codeToName(javaType);
        if (sqlTypeName == null) {
            throw new RuntimeException("Cannot convert type: " + type.getName());
        }
        return sqlTypeName;
    }

    @Override
    public Object getFieldValueFromResultSet(ResultSet resultSet, int columnIndex, int sqlType) throws SQLException {
        switch (sqlType) {
            case BIGINT:
                return resultSet.getLong(columnIndex);
            case INTEGER:
                return resultSet.getInt(columnIndex);
            case VARCHAR:
                return resultSet.getString(columnIndex);
            default:
                throw new UnsupportedOperationException("아직 변환 지원 안하는 타입입니다: " + sqlType);
        }
    }

    @Override
    public String autoIncrementDefinition() {
        return "AUTO_INCREMENT";
    }

    @Override
    public String primaryKeyDefinition() {
        return "PRIMARY KEY";
    }

    @Override
    public String nullableDefinition(boolean nullable) {
        return nullable ? "NULL" : "NOT NULL";
    }
}
