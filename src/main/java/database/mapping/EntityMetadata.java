package database.mapping;

import database.mapping.column.EntityColumn;
import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: 정리할 수 있는 메서드 있나 확인
// TODO: 책임 분리
public class EntityMetadata {
    private final Class<?> clazz;
    private final TableMetadata tableMetadata;
    private final ColumnsMetadata columnsMetadata;
    private final EntityAssociationMetadata entityAssociationMetadata;

    private EntityMetadata(Class<?> clazz, TableMetadata tableMetadata, ColumnsMetadata columnsMetadata,
                           EntityAssociationMetadata entityAssociationMetadata) {
        this.clazz = clazz;
        this.tableMetadata = tableMetadata;
        this.columnsMetadata = columnsMetadata;
        this.entityAssociationMetadata = entityAssociationMetadata;
    }

    public List<String> getAllColumnNamesWithAssociations() {
        List<String> allColumnsWithAssociation = new ArrayList<>();

        allColumnsWithAssociation.add(getPrimaryKeyName());
        allColumnsWithAssociation.addAll(getGeneralColumnNames());
        for (Association association : getAssociationRelatedToOtherEntities()) {
            allColumnsWithAssociation.add(association.getForeignKeyColumnName());
        }
        return allColumnsWithAssociation;
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


    // all fields

    public Field getFieldByColumnName(String columnName) {
        return columnsMetadata.getFieldByColumnName(columnName);
    }

    public Field getFieldByFieldName(String fieldName) {
        return columnsMetadata.getFieldByFieldName(fieldName);
    }

    // all columns

    public List<EntityColumn> getAllEntityColumns() {
        return columnsMetadata.getAllEntityColumns();
    }

    // primary key
    public PrimaryKeyEntityColumn getPrimaryKey() {
        return columnsMetadata.getPrimaryKey();
    }

    public String getPrimaryKeyName() {
        return this.getPrimaryKey().getColumnName();
    }

    public Long getPrimaryKeyValue(Object entity) {
        return columnsMetadata.getPrimaryKeyValue(entity);
    }

    public boolean requiresIdWhenInserting() {
        return columnsMetadata.isRequiredId();
    }

    // general columns

    public List<GeneralEntityColumn> getGeneralColumns() {
        return columnsMetadata.getGeneralColumns();
    }

    public List<String> getGeneralColumnNames() {
        return this.getGeneralColumns().stream()
                .map(GeneralEntityColumn::getColumnName)
                .collect(Collectors.toList());
    }

    public List<Association> getAssociations() {
        return entityAssociationMetadata.getAssociations();
    }

    public boolean hasAssociation() {
        return !entityAssociationMetadata.getAssociatedTypes().isEmpty();
    }

    public List<Association> getAssociationRelatedToOtherEntities() {
        return entityAssociationMetadata.getAssociationRelatedToOtherEntities();
    }
}
