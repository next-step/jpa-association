package database.mapping;

import database.dialect.Dialect;
import database.mapping.column.EntityColumn;

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

    private EntityMetadata(Class<?> clazz, TableMetadata tableMetadata, ColumnsMetadata columnsMetadata) {
        this.clazz = clazz;
        this.tableMetadata = tableMetadata;
        this.columnsMetadata = columnsMetadata;
    }

    /**
     * entities 목록 안에 this.clazz 와 관련된 필드가 있는지 찾아보고 돌려줌
     */
    public List<String> getJoinColumnDefinitions(Dialect dialect, List<Class<?>> entities) {
        List<Association> ret = getAssociationFromOtherEntities(entities);

        if (ret.isEmpty()) {
            return List.of();
        } else {
            List<String> definitions = new ArrayList<>();

            Association association = ret.get(0);
            String definition = association.toColumnDefinition(dialect);
            definitions.add(definition);

            return definitions;
        }
    }

    // XXX: ColumnMetadata 쪽으로 로직 옮기기
    private List<Association> getAssociationFromOtherEntities(List<Class<?>> entities) {
        List<Association> ret = new ArrayList<>();
        for (Class<?> entity : entities) {
            if (entity == clazz) continue;
            EntityMetadata entityMetadata = EntityMetadataFactory.get(entity);
            for (Association association : entityMetadata.getAssociations()) {
                if (association.getEntityType() == clazz) {
                    ret.add(association);
                }
            }
        }
        return ret;
    }

    static EntityMetadata fromClass(Class<?> clazz) {
        return new EntityMetadata(
                clazz,
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
        return columnsMetadata.getAssociations();
    }

    public boolean hasAssociation() {
        return !columnsMetadata.getAssociatedTypes().isEmpty();
    }
}
