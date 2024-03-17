package persistence.sql.constant;

public enum SqlConstant {

    EMPTY(""),
    BLANK(" "),
    COMMA(","),
    UNDER("_"),
    LINE_COMMA(",\n"),
    LINE("\n"),
    DOT("."),
    AND("AND");

    private final String value;

    SqlConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
