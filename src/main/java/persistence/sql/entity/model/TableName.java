package persistence.sql.entity.model;

import static persistence.sql.constant.SqlConstant.*;

public class TableName {

    private final String name;

    public TableName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAcronyms() {
        return name.replaceAll(UNDER.getValue(), EMPTY.getValue())
                .toLowerCase();
    }

    public String getAcronymsAndTableName() {
        return getName() +
                BLANK.getValue() +
                getAcronyms();
    }
}
