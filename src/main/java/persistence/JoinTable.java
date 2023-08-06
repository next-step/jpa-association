package persistence;

public class JoinTable {
    private final String name;
    private final String column;

    public JoinTable(String name, String column) {
        this.name = name;
        this.column = column;
    }


    public String name() {
        return name;
    }

    public String column() {
        return column;
    }
}
