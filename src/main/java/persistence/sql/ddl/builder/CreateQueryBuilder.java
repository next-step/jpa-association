package persistence.sql.ddl.builder;

public class CreateQueryBuilder {
  private static final String CREATE_SQL_QUERY = "CREATE TABLE %s (%s);";
  private static final String CREATE_IF_NOT_EXISTS_SQL_QUERY = "CREATE TABLE IF NOT EXISTS %s (%s);";

  public CreateQueryBuilder() {
  }

  public String createIfNotExistsCreateQuery(String tableName, String columns) {
    return String.format(CREATE_IF_NOT_EXISTS_SQL_QUERY, tableName, columns);
  }

  public String createCreateQuery(String tableName, String columns) {
    return String.format(CREATE_SQL_QUERY, tableName, columns);
  }
}
