package persistence.sql.component;

public enum JoinType {
    INNER_JOIN("inner join"),
    LEFT_JOIN("left join"),
    /* TODO */;
    private String value;

    JoinType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
