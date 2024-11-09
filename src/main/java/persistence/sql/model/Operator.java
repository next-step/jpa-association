package persistence.sql.model;

public enum Operator {
    equals("="),
    notEquals("!="),
    greaterThan(">"),
    greaterThanOrEquals(">="),
    lessThan("<"),
    lessThanOrEquals("<=");

    String value;

    Operator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
