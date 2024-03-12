package persistence.sql.column;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JoinTableColumns {

    private static final String COMMA = ", ";
    private final List<JoinTableColumn> values;

    public JoinTableColumns(List<JoinTableColumn> values) {
        this.values = new ArrayList<>(values);
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
        return values.stream()
                .anyMatch(joinTable -> !joinTable.getAssociationEntity().isLazy());
    }

    public boolean hasNotAssociation() {
        return values.isEmpty();
    }
}
