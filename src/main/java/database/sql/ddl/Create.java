package database.sql.ddl;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.mapping.column.EntityColumn;
import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Create {
    private final String tableName;
    private final List<EntityColumn> allEntityColumns;
    private final Dialect dialect;
    private final List<Association> associationRelatedToOtherEntities;

    public Create(Class<?> clazz, List<Class<?>> entities, Dialect dialect) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        this.tableName = entityMetadata.getTableName();

        allEntityColumns = entityMetadata.columnsMetadata.allEntityColumns;

        this.dialect = dialect;
        this.associationRelatedToOtherEntities = entityMetadata.getAssociationRelatedToOtherEntities(entities);
    }

    public Create(Class<?> clazz, Dialect dialect) {
        this(clazz, List.of(), dialect);
    }

    public String buildQuery() {
        List<String> newList = new ArrayList<>();

        allEntityColumns.forEach(
                entityColumn -> newList.add(getColumnDefinition2(entityColumn)));

        associationRelatedToOtherEntities.forEach(
                association -> newList.add(getColumnDefinition(association)));

        return String.format("CREATE TABLE %s (%s)", tableName, String.join(", ", newList));
    }

    private String getColumnDefinition2(EntityColumn entityColumn) {
        if (entityColumn.isPrimaryKeyField()) {
            return getColumnDefinition((PrimaryKeyEntityColumn) entityColumn);
        } else {
            return getColumnDefinition((GeneralEntityColumn) entityColumn);
        }
    }

    private String getColumnDefinition(PrimaryKeyEntityColumn entityColumn) {
        StringJoiner definitionJoiner = new StringJoiner(" ");
        definitionJoiner.add(entityColumn.getColumnName());
        definitionJoiner.add(dialect.convertToSqlTypeDefinition(entityColumn.getType(), entityColumn.getColumnLength()));
        if (entityColumn.isAutoIncrement()) {
            definitionJoiner.add(dialect.autoIncrementDefinition());
        }
        definitionJoiner.add(dialect.primaryKeyDefinition());
        return definitionJoiner.toString();
    }

    private String getColumnDefinition(GeneralEntityColumn entityColumn) {
        return new StringJoiner(" ")
                .add(entityColumn.getColumnName())
                .add(dialect.convertToSqlTypeDefinition(entityColumn.getType(), entityColumn.getColumnLength()))
                .add(dialect.nullableDefinition(entityColumn.isNullable()))
                .toString();
    }

    private String getColumnDefinition(Association associationColumn) {
        String foreignKeyColumnName = associationColumn.getForeignKeyColumnName();
        String foreignKeyColumnType = associationColumn.getForeignKeyColumnType(dialect);
        return foreignKeyColumnName + " " + foreignKeyColumnType + " NOT NULL";

    }
}
