package persistence.sql.constant;

public enum SqlConstant {

    COMMA(","),
    DOT("."),
    SPACE(" "),
    EMPTY(""),
    EQUALS("=");

    private final String value;

    SqlConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public StringBuilder concat(String left, String right) {
        return new StringBuilder(left).append(value).append(right);
    }
}
