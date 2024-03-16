package database.sql.ddl;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Create {
    private final String tableName;
    private final Dialect dialect;
    private final List<Association> associationRelatedToOtherEntities;
    private final PrimaryKeyEntityColumn primaryKey;
    private final List<GeneralEntityColumn> generalColumns;

    public Create(Class<?> clazz, List<Class<?>> entities, Dialect dialect) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        this.tableName = entityMetadata.getTableName();
        this.primaryKey = entityMetadata.getPrimaryKey();
        this.generalColumns = entityMetadata.getGeneralColumns();

        this.dialect = dialect;
        this.associationRelatedToOtherEntities = entityMetadata.getAssociationRelatedToOtherEntities(entities);
    }

    public Create(Class<?> clazz, Dialect dialect) {
        this(clazz, List.of(), dialect);
    }

    public String buildQuery() {
        List<String> newList = new ArrayList<>();

        newList.add(getPrimaryKeyColumnDefinition(primaryKey));
        generalColumns.forEach(generalEntityColumn -> newList.add(getGeneralColumnDefinition(generalEntityColumn)));
        associationRelatedToOtherEntities.forEach(association -> newList.add(getAssociationFieldDefinition(association)));

        return String.format("CREATE TABLE %s (%s)", tableName, String.join(", ", newList));
    }

    private String getPrimaryKeyColumnDefinition(PrimaryKeyEntityColumn entityColumn) {
        String columnName = entityColumn.getColumnName();
        Class<?> type = entityColumn.getType();
        Integer columnLength = entityColumn.getColumnLength();
        boolean autoIncrement = entityColumn.isAutoIncrement();

        StringJoiner definitionJoiner = new StringJoiner(" ");
        definitionJoiner.add(columnName);
        definitionJoiner.add(dialect.convertToSqlTypeDefinition(type, columnLength));

        if (autoIncrement) {
            definitionJoiner.add(dialect.autoIncrementDefinition());
        }
        definitionJoiner.add(dialect.primaryKeyDefinition());
        return definitionJoiner.toString();
    }

    private String getGeneralColumnDefinition(GeneralEntityColumn entityColumn) {
        String columnName = entityColumn.getColumnName();
        Class<?> type = entityColumn.getType();
        Integer columnLength = entityColumn.getColumnLength();
        boolean nullable = entityColumn.isNullable();
        return new StringJoiner(" ")
                .add(columnName)
                .add(dialect.convertToSqlTypeDefinition(type, columnLength))
                .add(dialect.nullableDefinition(nullable))
                .toString();
    }

    private String getAssociationFieldDefinition(Association associationColumn) {
        String foreignKeyColumnName = associationColumn.getForeignKeyColumnName();
        String foreignKeyColumnType = associationColumn.getForeignKeyColumnType(dialect);
        return foreignKeyColumnName + " " + foreignKeyColumnType + " NOT NULL";
    }
}
