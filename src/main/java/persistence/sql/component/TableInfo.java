package persistence.sql.component;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import persistence.sql.NameUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class TableInfo {
    private Class<?> entityClass;
    private String tableName;

    private TableInfo(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.tableName = NameUtils.getTableName(entityClass);
    }

    public static TableInfo from(Class<?> entityClass) {
        return new TableInfo(entityClass);
    }

    public String getTableName() {
        return tableName;
    }

    public ColumnInfo getIdColumn() {
        Field idColumnField = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow();
        return ColumnInfo.of(this, idColumnField);
    }

    public ColumnInfo getColumn(String columnName) {
        Field columnField = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> columnName.equals(NameUtils.getColumnName(field)))
                .findAny()
                .orElseThrow();
        return ColumnInfo.of(this, columnField);
    }

    public List<JoinInfo> getJoinInfos() {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(JoinColumn.class))
                .map(this::getJoinInfo)
                .toList();
    }

    private JoinInfo getJoinInfo(Field sourceTableJoinField) {
        return JoinInfo.of(sourceTableJoinField, this.getIdColumn(), getJoinTargetColumnInfo(sourceTableJoinField));
    }

    private ColumnInfo getJoinTargetColumnInfo(Field field) {
        String joinTargetColumnName = field.getAnnotation(JoinColumn.class).referencedColumnName();

        Type type = field.getGenericType();

        if (type instanceof ParameterizedType parameterizedType) {
            type = parameterizedType.getActualTypeArguments()[0];
        }

        if (type instanceof Class<?> clazz) {
            TableInfo joinTargetTableInfo = TableInfo.from((Class<?>) clazz);
            return joinTargetTableInfo.getColumn(joinTargetColumnName);
        }

        throw new RuntimeException("Unable to cast to appropriate class type!");
    }
}
