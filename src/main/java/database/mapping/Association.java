package database.mapping;

import java.lang.reflect.Type;
import java.util.List;

public class Association {

    private final String foreignKeyColumnName;
    private final Type entityType;

    public Association(String foreignKeyColumnName, Type entityType) {
        this.foreignKeyColumnName = foreignKeyColumnName;
        this.entityType = entityType;
    }

    public String getForeignKeyColumnName() {
        return foreignKeyColumnName;
    }

    public String getTableName() {
        return getEntityMetadata().getTableName();
    }

    public List<String> getColumnNames() {
        return getEntityMetadata().getAllColumnNames();
    }

    private EntityMetadata getEntityMetadata() {
        return EntityMetadataFactory.get(entityType);
    }
}
