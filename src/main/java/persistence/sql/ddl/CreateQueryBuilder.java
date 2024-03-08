package persistence.sql.ddl;

import java.util.stream.Collectors;
import static persistence.sql.constant.SqlConstant.COMMA;
import persistence.sql.dialect.Dialect;
import persistence.sql.meta.Table;

public class CreateQueryBuilder {

    private static final String CREATE_TABLE_DEFINITION = "CREATE TABLE %s (%s)";

    private final FieldQueryGenerator fieldBuilder;

    private CreateQueryBuilder(Dialect dialect) {
        fieldBuilder = FieldQueryGenerator.from(dialect);
    }

    public static CreateQueryBuilder from(Dialect dialect) {
        return new CreateQueryBuilder(dialect);
    }

    public String generateQuery(Table table) {
        String columnDefinitions = generateColumnDefinitions(table);
        String relationDefinitions = generateRelationDefinitions(table);
        return generateTableDefinition(table, columnDefinitions, relationDefinitions);
    }

    private String generateColumnDefinitions(Table table) {
        return table.getColumns()
            .stream()
            .map(fieldBuilder::generate)
            .collect(Collectors.joining(COMMA.getValue()));
    }

    private String generateRelationDefinitions(Table table) {
        return Table.getRelationColumns(table)
            .stream()
            .map(entry -> fieldBuilder.generateRelation(entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(COMMA.getValue()));
    }

    private String generateTableDefinition(Table table, String columnDefinitions, String relationDefinitions) {
        StringBuilder allDefinitions = new StringBuilder(columnDefinitions);
        if (!relationDefinitions.isEmpty()) {
            allDefinitions.append(COMMA.getValue());
            allDefinitions.append(relationDefinitions);
        }

        return String.format(CREATE_TABLE_DEFINITION, table.getTableName(), allDefinitions);
    }
}
