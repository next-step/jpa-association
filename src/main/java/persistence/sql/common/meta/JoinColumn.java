package persistence.sql.common.meta;

import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class JoinColumn {
    private TableName tableName;
    private Column column;

    public JoinColumn(TableName tableName, Column column) {
        this.tableName = tableName;
        this.column = column;
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

        return new JoinColumn(TableName.of(clazz), Column.of(field));
    }

    public String getTableName() {
        return tableName.getName();
    }

    public String getTableAlias() {
        return tableName.getAlias();
    }

    public String getJoinColumn() {
        return column.getName();
    }
}
