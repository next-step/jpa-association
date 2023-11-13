package persistence.sql.common.meta;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class JoinColumn {
    private TableName tableName;
    private Columns columns;
    private Column targetColumn;
    private FetchType fetchType;

    public JoinColumn(TableName tableName, Columns columns, Column targetColumn, FetchType fetchType) {
        this.tableName = tableName;
        this.columns = columns;
        this.targetColumn = targetColumn;
        this.fetchType = fetchType;
    }

    public static JoinColumn of(Field[] fields) {
        Field field = Arrays.stream(fields)
                .filter(f -> f.isAnnotationPresent(OneToMany.class))
                .findFirst()
                .orElse(null);

        if (field == null) {
            return null;
        }

        Class<?> clazz = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

        return new JoinColumn(TableName.of(clazz), Columns.of(clazz.getDeclaredFields()), Column.of(field), extractFetchType(field));
    }

    public static FetchType extractFetchType(Field field) {
        FetchType fetch = field.getAnnotation(OneToMany.class).fetch();

        if(fetch == null) {
            return FetchType.EAGER;
        }

        return fetch;
    }

    public String getTableName() {
        return tableName.getName();
    }

    public String getTableAlias() {
        return tableName.getAlias();
    }

    public String getJoinColumn() {
        return targetColumn.getName();
    }

    public Columns getColumns() {
        return columns;
    }

    public boolean isEager() {
        return fetchType.equals(FetchType.EAGER);
    }
}
