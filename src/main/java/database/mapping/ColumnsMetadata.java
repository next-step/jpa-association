package database.mapping;

import database.dialect.Dialect;
import database.mapping.column.EntityColumn;
import database.mapping.column.EntityColumnFactory;
import database.mapping.column.PrimaryKeyEntityColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColumnsMetadata {
    private final Class<?> clazz;
    private final List<EntityColumn> allEntityColumns;
    private final PrimaryKeyEntityColumn primaryKey;
    private final List<EntityColumn> generalColumns;
    private final Map<String, Field> fieldByColumnNameMap;
    private final boolean isRequiredId;

    private ColumnsMetadata(Class<?> clazz,
                            List<EntityColumn> allEntityColumns,
                            PrimaryKeyEntityColumn primaryKey,
                            List<EntityColumn> generalColumns,
                            Map<String, Field> fieldByColumnNameMap,
                            boolean isRequiredId) {
        this.clazz = clazz;
        this.allEntityColumns = allEntityColumns;
        this.primaryKey = primaryKey;
        this.generalColumns = generalColumns;
        this.fieldByColumnNameMap = fieldByColumnNameMap;
        this.isRequiredId = isRequiredId;
    }

    public static ColumnsMetadata fromClass(Class<?> clazz) {
        // TODO: ColumnsMetadataInspector 를 만들어볼까?
        List<Field> allFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !isRealColumn(field))
                .collect(Collectors.toList());

        List<EntityColumn> allEntityColumns = allFields.stream().map(EntityColumnFactory::fromField)
                .collect(Collectors.toList());

        Field primaryKeyField = allFields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst().get();

        PrimaryKeyEntityColumn primaryKey = (PrimaryKeyEntityColumn) EntityColumnFactory.fromField(primaryKeyField);

        boolean isRequiredId = !primaryKeyField.isAnnotationPresent(GeneratedValue.class);
        List<EntityColumn> generalColumns = allFields.stream()
                .map(EntityColumnFactory::fromField)
                .filter(columnMetadata -> !columnMetadata.isPrimaryKeyField())
                .collect(Collectors.toList());

        // TODO: H2 에서는 ResultSet 에서 돌아온 결과의 컬럼명이 대문자로 구성되어 있어서, 쉬운 비교를 위해서 미리 변환해서 저장해둠.
        // dialect 마다 상황이 다를 수도 있음. (대소문자를 구별해서 nick_name과 NICK_NAME 을 다르게 처리하는 경우에는 에러 발생한다)

        Map<String, Field> fieldByColumnNameMap = allFields.stream()
                .map(EntityColumnFactory::fromField)
                .collect(Collectors.toMap(
                        entityColumn -> entityColumn.getColumnName().toUpperCase(),
                        EntityColumn::getField)
                );

        return new ColumnsMetadata(clazz,
                                   allEntityColumns,
                                   primaryKey,
                                   generalColumns,
                                   fieldByColumnNameMap,
                                   isRequiredId
        );
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

    public PrimaryKeyEntityColumn getPrimaryKey() {
        return primaryKey;
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

    // TODO: columnname->fieldname 맵만 있으면 fieldByColumnNameMap 필요없네?
    public Field getFieldByColumnName(String columnName) {
        String upperCase = columnName.toUpperCase();
        return fieldByColumnNameMap.get(upperCase);
    }

    public Field getFieldByFieldName(String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRequiredId() {
        return isRequiredId;
    }
}
