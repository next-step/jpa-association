package persistence.sql.component;

import java.util.List;

public class ConditionBuilder {
    private ColumnInfo columnInfo;
    private List<String> values;
    private Condition andCondition;
    private Condition orCondition;

    public ConditionBuilder columnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
        return this;
    }

    public ConditionBuilder values(List<String> values) {
        this.values = values;
        return this;
    }

    public ConditionBuilder andCondition(Condition andCondition) {
        this.andCondition = andCondition;
        return this;
    }

    public ConditionBuilder orCondition(Condition orCondition) {
        this.orCondition = orCondition;
        return this;
    }

    public Condition build() {
        Condition condition = new Condition(columnInfo, values);
        if (andCondition != null) {
            condition.setAndCondition(andCondition);
        }
        if (orCondition != null) {
            condition.setOrCondition(orCondition);
        }
        return condition;
    }
}
