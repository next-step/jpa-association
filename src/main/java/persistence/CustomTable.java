package persistence;

public class CustomTable implements Node {
    private final String name;

    public CustomTable(String name) {
        this.name = name;
    }

    public static CustomTable of(Class<?> clazz) {
        return new CustomTable(tableName(clazz));
    }

    private static String tableName(Class<?> clazz) {
        jakarta.persistence.Table table = clazz.getAnnotation(jakarta.persistence.Table.class);
        
        if (table == null) {
            return clazz.getSimpleName().toLowerCase();
        }
        return table.name();
    }

    @Override
    public String expression() {
        return name;
    }

    public String name() {
        return name;
    }
}
