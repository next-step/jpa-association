package persistence.entity.metadata;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import persistence.inspector.EntityFieldInspector;
import persistence.inspector.EntityInfoExtractor;
import persistence.sql.DataType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMetadataBuilder {

    static DataType dataType = new DataType();

    public static EntityMetadata build(Class<?> clazz) {
        EntityMetadata metadata = new EntityMetadata();
        metadata.setEntityTable(new EntityTable(clazz.getSimpleName(), EntityInfoExtractor.getTableName(clazz)));
        metadata.setColumns(new EntityColumns(buildColumns(metadata.getTableName(), clazz)));
        metadata.setRelationEntityTables(buildRelationEntities(clazz));

        return metadata;
    }

    private static List<RelationEntityTable> buildRelationEntities(Class<?> clazz) {
        return EntityInfoExtractor.getAllFields(clazz)
                .stream()
                .filter(EntityInfoExtractor::isRelationshipField)
                .map(EntityMetadataBuilder::buildRelationEntityMetadata)
                .collect(Collectors.toList());
    }

    private static RelationEntityTable buildRelationEntityMetadata(Field field) {
        Class<?> fieldClassType = EntityInfoExtractor.getFieldClassType(field);
        String joinColumnName = field.getAnnotation(JoinColumn.class).name();
        if (field.isAnnotationPresent(OneToMany.class)) {
            return new RelationEntityTable(RelationType.ONE_TO_MANY, fieldClassType, field, joinColumnName);
        } else if (field.isAnnotationPresent(ManyToOne.class)) {
            return new RelationEntityTable(RelationType.MANY_TO_ONE, fieldClassType, field, joinColumnName);
        }
        return null;
    }

    private static List<EntityColumn> buildColumns(String tableName, Class<?> clazz) {
        return EntityInfoExtractor.getColumns(clazz).stream()
                .map(column -> buildColumn(tableName, column))
                .collect(Collectors.toList());
    }

    private static EntityColumn buildColumn(String tableName, Field field) {
        EntityColumn column = new EntityColumn();
        column.setTableName(tableName);
        column.setField(field);
        column.setFieldName(field.getName());
        column.setColumnName(EntityInfoExtractor.getColumnName(field));
        column.setSqlTypeCode(dataType.getSqlTypeCode(field.getType()));
        column.setPrimaryKey(EntityInfoExtractor.isPrimaryKey(field));
        column.setNullable(EntityFieldInspector.isNullable(field));
        column.setGenerationType(EntityFieldInspector.getGenerationType(field));
        column.setLength(EntityFieldInspector.getLength(field));

        return column;
    }

}
