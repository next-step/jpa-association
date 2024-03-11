package database.mapping;

import database.dialect.Dialect;
import database.mapping.column.EntityColumn;
import database.mapping.column.EntityColumnFactory;
import jakarta.persistence.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColumnsMetadata {
    private final List<EntityColumn> allEntityColumns;
    private final EntityColumn primaryKey;
    private final List<EntityColumn> generalColumns;
    private final Map<String, Field> fieldByColumnNameMap;
    private final boolean isRequiredId;
    private final List<Field> associationFields;

    private ColumnsMetadata(List<EntityColumn> allEntityColumns,
                            EntityColumn primaryKey,
                            List<EntityColumn> generalColumns,
                            Map<String, Field> fieldByColumnNameMap,
                            boolean isRequiredId,
                            List<Field> associationFields) {
        this.allEntityColumns = allEntityColumns;
        this.primaryKey = primaryKey;
        this.generalColumns = generalColumns;
        this.fieldByColumnNameMap = fieldByColumnNameMap;
        this.isRequiredId = isRequiredId;
        this.associationFields = associationFields;
    }

    public static ColumnsMetadata fromClass(Class<?> clazz) {
        // XXX: ColumnsMetadataInspector 를 만들어볼까?
        List<Field> allFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !isRealColumn(field))
                .collect(Collectors.toList());

        List<EntityColumn> allEntityColumns = allFields.stream().map(EntityColumnFactory::fromField)
                .collect(Collectors.toList());

        Field primaryKeyField = allFields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst().get();

        EntityColumn primaryKey = EntityColumnFactory.fromField(primaryKeyField);

        boolean isRequiredId = !primaryKeyField.isAnnotationPresent(GeneratedValue.class);
        List<EntityColumn> generalColumns = allFields.stream()
                .map(EntityColumnFactory::fromField)
                .filter(columnMetadata -> !columnMetadata.isPrimaryKeyField())
                .collect(Collectors.toList());

        List<Field> associationFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .collect(Collectors.toList());

        // TODO: H2 에서는 ResultSet 에서 돌아온 결과의 컬럼명이 대문자로 구성되어 있어서, 쉬운 비교를 위해서 미리 변환해서 저장해둠.
        // dialect 마다 상황이 다를 수도 있음. (대소문자를 구별해서 nick_name과 NICK_NAME 을 다르게 처리하는 경우에는 에러 발생한다)

        Map<String, Field> fieldByColumnNameMap = allFields.stream()
                .map(EntityColumnFactory::fromField)
                .collect(Collectors.toMap(
                        entityColumn -> entityColumn.getColumnName().toUpperCase(),
                        EntityColumn::getField)
                );

        return new ColumnsMetadata(allEntityColumns,
                                   primaryKey,
                                   generalColumns,
                                   fieldByColumnNameMap,
                                   isRequiredId,
                                   associationFields);
    }

    public List<Association> getAssociations() {
        return associationFields.stream()
                .filter(ColumnsMetadata::checkAssociationAnnotation)
                .map(field -> {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    String foreignKeyColumnName = joinColumn.name();
                    Type[] actualTypeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                    Type type = actualTypeArguments[0];
                    return new Association(foreignKeyColumnName, type);
                })
                .collect(Collectors.toList());
    }

    private static boolean checkAssociationAnnotation(Field field) {
        return field.isAnnotationPresent(OneToMany.class) && field.isAnnotationPresent(JoinColumn.class);
    }

    private static boolean isRealColumn(Field field) {
        return field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(OneToMany.class);
    }

    public List<String> getAllColumnNames() {
        return allEntityColumns.stream().map(EntityColumn::getColumnName).collect(Collectors.toList());
    }

    public List<String> getColumnDefinitions(Dialect dialect) {
        return allEntityColumns.stream()
                .map(entityColumn -> entityColumn.toColumnDefinition(dialect))
                .collect(Collectors.toList());
    }

    public String getPrimaryKeyColumnName() {
        return primaryKey.getColumnName();
    }

    public List<String> getGeneralColumnNames() {
        return generalColumns.stream().map(EntityColumn::getColumnName).collect(Collectors.toList());
    }

    public List<EntityColumn> getGeneralColumns() {
        return generalColumns;
    }

    public Long getPrimaryKeyValue(Object entity) {
        return (Long) primaryKey.getValue(entity);
    }

    public Field getFieldByColumnName(String columnName) {
        String upperCase = columnName.toUpperCase();
        return fieldByColumnNameMap.get(upperCase);
    }

    public boolean isRequiredId() {
        return isRequiredId;
    }
}
