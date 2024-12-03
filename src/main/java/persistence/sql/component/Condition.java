package persistence.sql.component;

import java.util.List;

public class Condition {
    private ColumnInfo columnInfo;
    private List<String> values;
    private Condition andCondition;
    private Condition orCondition;

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public List<String> getValues() {
        return values;
    }

    public Condition getAndCondition() {
        return andCondition;
    }

    public Condition getOrCondition() {
        return orCondition;
    }

    public Condition(ColumnInfo columnInfo, List<String> values) {
        this.columnInfo = columnInfo;
        this.values = values;
    }

    public void setAndCondition(Condition andCondition) {
        this.andCondition = andCondition;
    }

    public void setOrCondition(Condition orCondition) {
        this.orCondition = orCondition;
    }
}