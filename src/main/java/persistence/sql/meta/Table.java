package persistence.sql.meta;

import jakarta.persistence.Entity;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.h2.util.StringUtils;

public class Table {

    private final Class<?> clazz;
    private final Columns columns;
    private static final Map<Class<?>, Table> cashTable = new ConcurrentHashMap<>();
    private static final Map<Table, Set<Map.Entry<Table, Column>>> relationTable = new ConcurrentHashMap<>();

    private Table(Class<?> clazz, Columns columns) {
        this.clazz = clazz;
        this.columns = columns;
    }

    public static Table getInstance(Class<?> clazz) {
        if (cashTable.containsKey(clazz)) {
            return cashTable.get(clazz);
        }
        validate(clazz);

        Columns columns = Columns.from(clazz.getDeclaredFields());
        Table table = cashTable.computeIfAbsent(clazz, t -> new Table(clazz, columns));
        setRelationTable(table, columns);

        return table;
    }

    public static Set<Map.Entry<Table, Column>> getRelationColumns(Table table) {
        return relationTable.getOrDefault(table, Set.of());
    }

    public List<Column> getColumns() {
        return columns.getSelectColumns();
    }

    public String getTableName() {
        jakarta.persistence.Table table = clazz.getAnnotation(jakarta.persistence.Table.class);
        if (table == null || StringUtils.isNullOrEmpty(table.name())) {
            return clazz.getSimpleName();
        }
        return table.name();
    }

    public Object getClassInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("클래스 인스턴스 생성에 실패했습니다.");
        }
    }

    public List<Column> getInsertColumns() {
        return columns.getInsertColumns();
    }

    public List<Column> getUpdateColumns() {
        return columns.getUpdateColumns();
    }

    public List<Column> getSelectColumns() {
        return columns.getSelectColumns();
    }

    public List<Column> getEagerRelationColumns() {
        return columns.getEagerRelationColumns();
    }

    public boolean isEagerRelationEmpty() {
        return columns.getRelationColumns().isEmpty();
    }

    public List<Table> getEagerRelationTables() {
        return columns.getEagerRelationColumns()
            .stream().map(Column::getRelationTable)
            .collect(Collectors.toList());
    }

    public Column getIdColumn() {
        return columns.getIdColumn();
    }

    public String getIdColumnName() {
        return columns.getIdColumn().getColumnName();
    }

    public Object getIdValue(Object entity) {
        return columns.getIdValue(entity);
    }

    public void setIdValue(Object entity, Object id) {
        columns.getIdColumn().setFieldValue(entity, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(clazz, table.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    public List<Object> getRelationValues(Object entity) {
        return columns.getRelationValues(entity);
    }

    private static void setRelationTable(Table root, Columns columns) {
        columns.getRelationColumns().stream()
            .filter(Column::isOneToMany)
            .forEach(column -> {
                Table table = column.getRelationTable();
                if (!relationTable.containsKey(table)) {
                    relationTable.put(table, new HashSet<>());
                }
                relationTable.get(table).add(Map.entry(root, column));
            });
    }

    private static void validate(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("엔티티 객체가 아닙니다.");
        }
    }
}
