package persistence.sql.column.type;

public class NullableType {

    private final boolean isNullable;

    public NullableType() {
        this(true);
    }

    public NullableType(boolean isNullable) {
        this.isNullable = isNullable;
    }

    public String getDefinition() {
        if (isNullable) {
            return "";
        }
        return " not null";
    }
}
