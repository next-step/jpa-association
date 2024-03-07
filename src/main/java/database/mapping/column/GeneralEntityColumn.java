package database.mapping.column;

import database.dialect.Dialect;
import jakarta.persistence.Column;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class GeneralEntityColumn extends AbstractEntityColumn {
    private static final boolean DEFAULT_NULLABLE = true;

    private final boolean nullable;

    private GeneralEntityColumn(Field field,
                                String columnName,
                                Class<?> type,
                                Integer columnLength,
                                boolean nullable) {
        super(field, columnName, type, columnLength);
        this.nullable = nullable;
    }

    public static GeneralEntityColumn create(Field field) {
        String columnName = getColumnName(field);
        Integer columnLength = getColumnLength(field);
        Class<?> type = field.getType();
        boolean nullable = isNullable(field);

        return new GeneralEntityColumn(field, columnName, type, columnLength, nullable);
    }

    @Override
    public String toColumnDefinition(Dialect dialect) {
        return new StringJoiner(" ")
                .add(columnName)
                .add(dialect.convertToSqlTypeDefinition(type, columnLength))
                .add(dialect.nullableDefinition(nullable))
                .toString();
    }

    @Override
    public boolean isPrimaryKeyField() {
        return false;
    }

    private static boolean isNullable(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null) {
            return columnAnnotation.nullable();
        }
        return DEFAULT_NULLABLE;
    }
}
