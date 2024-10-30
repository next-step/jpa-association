package builder.dml;

public class AliasColumn {

    private static final String UNDER_BAR = "_";
    private static final String DOT = ".";
    private static final String BLANK = " ";

    public static String aliasTable(String table) {
        return table + BLANK + table + UNDER_BAR;
    }

    public static String aliasColumn(String table, String column) {
        return table + UNDER_BAR + DOT + column;
    }
}
