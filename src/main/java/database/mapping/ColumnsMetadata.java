package database.mapping;

import database.mapping.column.EntityColumn;
import database.mapping.column.EntityColumnFactory;
import database.mapping.column.GeneralEntityColumn;
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

    private ColumnsMetadata(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static ColumnsMetadata fromClass(Class<?> clazz) {
        return new ColumnsMetadata(clazz);
    }

    // ---------- all columns -----------

    private List<Field> getAllFields() {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !isRealColumn(field))
                .collect(Collectors.toList());
    }

    private static boolean isRealColumn(Field field) {
        return field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(OneToMany.class);
    }

    public List<EntityColumn> getAllEntityColumns() {
        return this.getAllFields().stream()
                .map(EntityColumnFactory::fromField)
                .collect(Collectors.toList());
    }

    // ---------- primary key -----------

    private Field getPrimaryKeyField() {
        return this.getAllFields().stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst().get();
    }

    public boolean isRequiredId() {
        return !this.getPrimaryKeyField().isAnnotationPresent(GeneratedValue.class);
    }

    public PrimaryKeyEntityColumn getPrimaryKey() {
        return (PrimaryKeyEntityColumn) EntityColumnFactory.fromField(this.getPrimaryKeyField());
    }

    public Long getPrimaryKeyValue(Object entity) {
        return (Long) this.getPrimaryKey().getValue(entity);
    }

    // ---------- general columns -----------

    public List<GeneralEntityColumn> getGeneralColumns() {
        return this.getAllFields().stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .collect(Collectors.toList()).stream()
                .map(EntityColumnFactory::fromGeneralField)
                .collect(Collectors.toList());
    }

    // ---------- utility functions for each column -----------

    public Field getFieldByColumnName(String columnName) {
        // TODO: columnname->fieldname 맵만 있으면 fieldByColumnNameMap 필요없네?
        // TODO: H2 에서는 ResultSet 에서 돌아온 결과의 컬럼명이 대문자로 구성되어 있어서, 쉬운 비교를 위해서 미리 변환해서 저장해둠.
        // dialect 마다 상황이 다를 수도 있음. (대소문자를 구별해서 nick_name과 NICK_NAME 을 다르게 처리하는 경우에는 에러 발생한다)

        String upperCase = columnName.toUpperCase();
        return this.getFieldByColumnNameMap().get(upperCase);
    }

    private Map<String, Field> getFieldByColumnNameMap() {
        return this.getAllFields().stream()
                .map(EntityColumnFactory::fromField)
                .collect(Collectors.toMap(
                        entityColumn -> entityColumn.getColumnName().toUpperCase(),
                        EntityColumn::getField)
                );
    }

    public Field getFieldByFieldName(String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
