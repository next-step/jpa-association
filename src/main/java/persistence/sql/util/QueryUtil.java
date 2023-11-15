package persistence.sql.util;

public class QueryUtil {
	public static String convertValueToString(Object value) {
		if(value.getClass().equals(String.class)) {
			return "'" + value + "'";
		}

		return String.valueOf(value);
	}

	public static String convertColumnToString(String tableName, String columnName) {
		return tableName + "." + columnName;
	}
}
