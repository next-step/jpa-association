package database.sql.ddl;

import database.dialect.Dialect;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;

import java.util.ArrayList;
import java.util.List;

public class Create {
    private final String tableName;
    private final List<String> columnDefinitions;
    private final List<String> joinColumnDefinitions;

    public Create(Class<?> clazz, Dialect dialect) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        this.tableName = entityMetadata.getTableName();
        this.columnDefinitions = entityMetadata.getColumnDefinitions(dialect);
        this.joinColumnDefinitions = List.of();
    }

    public Create(Class<?> clazz, List<Class<?>> entities, Dialect dialect) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(clazz);
        this.tableName = entityMetadata.getTableName();
        this.columnDefinitions = entityMetadata.getColumnDefinitions(dialect);
        this.joinColumnDefinitions = entityMetadata.getJoinColumnDefinitions(dialect, entities);
    }

    public String buildQuery() {
        List<String> newList = new ArrayList<>();
        newList.addAll(columnDefinitions);
        newList.addAll(joinColumnDefinitions);
        String columnsWithDefinition1 = String.join(", ", newList);

        return String.format("CREATE TABLE %s (%s)", tableName, columnsWithDefinition1);
    }
}
