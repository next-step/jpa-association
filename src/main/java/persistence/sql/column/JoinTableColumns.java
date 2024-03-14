package persistence.sql.column;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JoinTableColumns {

    private static final String COMMA = ", ";
    private final List<JoinTableColumn> values;

    private boolean hasEager;
    private boolean hasLazy;

    public JoinTableColumns(List<JoinTableColumn> values) {
        this.values = new ArrayList<>(values);
        this.hasEager = values.stream()
                .anyMatch(joinTable -> !joinTable.getAssociationEntity().isLazy());
        this.hasLazy = values.stream()
                .anyMatch(joinTable -> joinTable.getAssociationEntity().isLazy());
    }

    public List<JoinTableColumn> getValues() {
        return values;
    }

    public String getAssociationColumnsDefinition() {
        return values.stream()
                .map(JoinTableColumn::getColumnDefinition)
                .collect(Collectors.joining(COMMA));
    }

    public String getJoinDefinition(String rootTableName) {
        return values.stream()
                .map(joinTableColumn -> joinTableColumn.getJoinClauseWithAssociation(rootTableName))
                .collect(Collectors.joining());
    }

    public JoinTableColumns getEagerJoinTables() {
        List<JoinTableColumn> eagerJoinTables = values.stream()
                .filter(joinTable -> !joinTable.getAssociationEntity().isLazy())
                .collect(Collectors.toList());
        return new JoinTableColumns(eagerJoinTables);
    }

    public List<JoinTableColumn> getLazyJoinTables() {
        return values.stream()
                .filter(joinTable -> joinTable.getAssociationEntity().isLazy())
                .collect(Collectors.toList());
    }

    public boolean hasEager() {
        return hasEager;
    }

    public boolean hasLazy() {
        return hasLazy;
    }

    public boolean hasNotAssociation() {
        return values.isEmpty();
    }
}
