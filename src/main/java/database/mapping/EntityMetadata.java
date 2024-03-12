package database.mapping;

import database.dialect.Dialect;
import database.mapping.column.EntityColumn;

import java.lang.reflect.Field;
import java.util.List;

// TODO: 정리할 수 있는 메서드 있나 확인
// TODO: 책임 분리
public class EntityMetadata {
    private final TableMetadata tableMetadata;
    private final ColumnsMetadata columnsMetadata;

    private EntityMetadata(TableMetadata tableMetadata, ColumnsMetadata columnsMetadata) {
        this.tableMetadata = tableMetadata;
        this.columnsMetadata = columnsMetadata;
    }

    static EntityMetadata fromClass(Class<?> clazz) {
        return new EntityMetadata(
                new TableMetadata(clazz),
                ColumnsMetadata.fromClass(clazz)
        );
    }

    public String getTableName() {
        return tableMetadata.getTableName();
    }

    public String getEntityClassName() {
        return tableMetadata.getEntityClassName();
    }

    public List<String> getAllColumnNames() {
        return columnsMetadata.getAllColumnNames();
    }

    public String getJoinedAllColumnNames() {
        return String.join(", ", columnsMetadata.getAllColumnNames());
    }

    public List<String> getColumnDefinitions(Dialect dialect) {
        return columnsMetadata.getColumnDefinitions(dialect);
    }

    public String getPrimaryKeyColumnName() {
        return columnsMetadata.getPrimaryKeyColumnName();
    }

    public List<String> getGeneralColumnNames() {
        return columnsMetadata.getGeneralColumnNames();
    }

    public List<EntityColumn> getGeneralColumns() {
        return columnsMetadata.getGeneralColumns();
    }

    public Long getPrimaryKeyValue(Object entity) {
        return columnsMetadata.getPrimaryKeyValue(entity);
    }

    public Field getFieldByColumnName(String columnName) {
        return columnsMetadata.getFieldByColumnName(columnName);
    }

    public Field getFieldByFieldName(String fieldName) {
        return columnsMetadata.getFieldByFieldName(fieldName);
    }

    public boolean requiresIdWhenInserting() {
        return columnsMetadata.isRequiredId();
    }

    public List<Association> getAssociations() {
        return columnsMetadata.getAssociations();
    }

    public boolean hasAssociation() {
        return !columnsMetadata.getAssociatedTypes().isEmpty();
    }
}
