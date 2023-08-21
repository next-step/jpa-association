package persistence.entity;

public class RootTable {
    private final String name;
    private final UniqueColumn uniqueColumn;

    public RootTable(String rootTable, UniqueColumn rootColumn) {
        this.name = rootTable;
        this.uniqueColumn = rootColumn;
    }

    public String name() {
        return name;
    }

    public String uniqueColumn() {
        return uniqueColumn.name();
    }
}
