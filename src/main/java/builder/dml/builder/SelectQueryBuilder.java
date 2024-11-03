package builder.dml.builder;

public class SelectQueryBuilder {

    private static final String SELECT = "SELECT";
    private static final String BLANK = " ";
    private static final String FROM = "FROM";
    private static final String WHERE = "WHERE";
    private static final String EQUALS = "=";
    private static final String JOIN = "JOIN";
    private static final String ON = "ON";
    private static final String SEMI_COLON = ";";

    private final StringBuilder stringBuilder = new StringBuilder();

    public SelectQueryBuilder select(String columns) {
        stringBuilder.append(SELECT)
                .append(BLANK)
                .append(columns);

        return this;
    }

    public SelectQueryBuilder from(String tableName) {
        stringBuilder
                .append(BLANK)
                .append(FROM)
                .append(BLANK)
                .append(tableName);

        return this;
    }

    public SelectQueryBuilder where(String pkName, String pkValue) {
        stringBuilder
                .append(BLANK)
                .append(WHERE)
                .append(BLANK)
                .append(pkName)
                .append(BLANK)
                .append(EQUALS)
                .append(BLANK)
                .append(pkValue);

        return this;
    }

    public SelectQueryBuilder join(String joinTableName) {
        stringBuilder
                .append(BLANK)
                .append(JOIN)
                .append(BLANK)
                .append(joinTableName);

        return this;
    }

    public SelectQueryBuilder on(String mainPk, String joinPk) {
        stringBuilder
                .append(BLANK)
                .append(ON)
                .append(BLANK)
                .append(mainPk)
                .append(BLANK)
                .append(EQUALS)
                .append(BLANK)
                .append(joinPk);

        return this;
    }

    public String build() {
        return stringBuilder
                .append(SEMI_COLON)
                .toString();
    }

}
