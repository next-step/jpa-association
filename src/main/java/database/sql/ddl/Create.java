package database.sql.ddl;

import database.dialect.Dialect;
import database.mapping.EntityMetadata;

public class Create {
    private final String tableName;
    private final String columnsWithDefinition;

    public Create(EntityMetadata entityMetadata, Dialect dialect) {
        this.tableName = entityMetadata.getTableName();
        this.columnsWithDefinition = String.join(", ", entityMetadata.getColumnDefinitions(dialect));
    }

    public Create(Class<?> clazz, Dialect dialect) {
        this(EntityMetadata.fromClass(clazz), dialect);
    }

    public String buildQuery() {
        return String.format("CREATE TABLE %s (%s)", tableName, columnsWithDefinition);
    }
}
