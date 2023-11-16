package persistence.sql.dml.builder;

import java.util.ArrayList;
import java.util.List;

public class JoinQuery {

  private static final String JOIN_QUERY = "%s %s %s %s;";
  private static final String SELECT_CLAUSE = "SELECT %s";
  private static final String FROM_CLAUSE = "FROM %s";
  private static final String JOIN_CLAUSE = "JOIN %s ON %s = %s";
  private static final String WHERE_CLAUSE = "WHERE %s IN [%s]";
  private static final String DELIMITER = ",";
  private static final String SPACE = " ";
  private final String tableName;
  private final List<String> tablesToJoin;
  private final List<String> foreignKey;
  private final String targetName;
  private final List<String> targets;
  private final String selectClause;


  public JoinQuery(String selectClause, String tableName, List<String> tablesToJoin,
      List<String> foreignKey, String targetName, List<String> targets) {
    this.selectClause = selectClause;
    this.tableName = tableName;
    this.tablesToJoin = tablesToJoin;
    this.foreignKey = foreignKey;
    this.targetName = targetName;
    this.targets = targets;
  }

  public String createJoinQuery(){

    String select = String.format(SELECT_CLAUSE, selectClause);
    String from = String.format(FROM_CLAUSE, tableName);

    List<String> joins = new ArrayList<>();

    for (int i = 0; i < tablesToJoin.size(); i++) {
      joins.add(String.format(JOIN_CLAUSE, tablesToJoin.get(i), targetName,  foreignKey.get(i)));
    }

    String join = String.join(SPACE, joins);

    String ids = String.join(DELIMITER, targets);
    String whereClause = String.format(WHERE_CLAUSE, targetName, ids);

    return String.format(JOIN_QUERY, select, from, join, whereClause);
  }
}
