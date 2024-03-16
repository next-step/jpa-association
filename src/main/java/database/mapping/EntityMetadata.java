package database.mapping;

import database.mapping.column.EntityColumn;

import java.lang.reflect.Field;
import java.util.List;

// TODO: 정리할 수 있는 메서드 있나 확인
// TODO: 책임 분리
public class EntityMetadata {
    private final Class<?> clazz;
    private final TableMetadata tableMetadata;
    public final ColumnsMetadata columnsMetadata; // XXX: 임시
    private final EntityAssociationMetadata entityAssociationMetadata;

    private EntityMetadata(Class<?> clazz, TableMetadata tableMetadata, ColumnsMetadata columnsMetadata,
                           EntityAssociationMetadata entityAssociationMetadata) {
        this.clazz = clazz;
        this.tableMetadata = tableMetadata;
        this.columnsMetadata = columnsMetadata;
        this.entityAssociationMetadata = entityAssociationMetadata;
    }

    static EntityMetadata fromClass(Class<?> clazz) {
        return new EntityMetadata(
                clazz,
                new TableMetadata(clazz),
                ColumnsMetadata.fromClass(clazz),
                new EntityAssociationMetadata(clazz)
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

    public String getPrimaryKeyColumnName() {
        return columnsMetadata.getPrimaryKeyColumnName();
    }

    public List<String> getGeneralColumnNames() {
        return columnsMetadata.getGeneralColumnNames();
    }

    public List<EntityColumn> getGeneralColumns() {
        return columnsMetadata.getGeneralColumns();
    }

    public EntityColumn getPrimaryKey() {
        return columnsMetadata.getPrimaryKey();
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
        return entityAssociationMetadata.getAssociations();
    }

    public boolean hasAssociation() {
        return !entityAssociationMetadata.getAssociatedTypes().isEmpty();
    }

    public List<Association> getAssociationRelatedToOtherEntities(List<Class<?>> entities) {
        return entityAssociationMetadata.getAssociationRelatedToOtherEntities(entities);
    }
}
