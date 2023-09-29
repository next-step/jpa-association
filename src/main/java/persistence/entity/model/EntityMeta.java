package persistence.entity.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMeta {
    private final Class<?> entityClass;
    private final String tableName;
    private final EntityColumn idColumn;
    private final EntityColumns normalColumns;
    private final OneToManyColumn oneToManyColumn;

    public EntityMeta(
            Class<?> entityClass,
            String tableName,
            EntityColumn idColumn,
            EntityColumns normalColumns,
            OneToManyColumn oneToManyColumn
    ) {
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.normalColumns = normalColumns;
        this.oneToManyColumn = oneToManyColumn;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumnNames() {
        List<String> columns = new ArrayList<>();
        columns.add(idColumn.getName());
        columns.addAll(normalColumns.getNames());

        return columns.stream()
                .map(it -> String.format("%s.%s", tableName, it))
                .collect(Collectors.toList());
    }

    public EntityColumn getIdColumn() {
        return idColumn;
    }

    public Class<?> getOneToManyColumnClass() {
        return oneToManyColumn.getEntityClass();
    }

    public EntityColumns getNormalColumns() {
        return normalColumns;
    }

    public boolean isNew(Object entity) {
        Object value = idColumn.getValue(entity);
        return value == null || (Long) value == 0;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public boolean isLazyLoading() {
        return oneToManyColumn != null && oneToManyColumn.isLazy();
    }

    public boolean isEagerLoading() {
        return oneToManyColumn != null && oneToManyColumn.isEager();
    }

    public String getForeignKeyName() {
        return oneToManyColumn.getForeignKeyName();
    }
}

