package database.mapping.column;

import database.dialect.Dialect;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class PrimaryKeyEntityColumn extends AbstractEntityColumn {
    private final boolean autoIncrement;

    private PrimaryKeyEntityColumn(Field field,
                                   String columnName,
                                   Class<?> type,
                                   Integer columnLength,
                                   boolean autoIncrement) {
        super(field, columnName, type, columnLength);
        this.autoIncrement = autoIncrement;
    }

    public static PrimaryKeyEntityColumn create(Field field) {
        String columnName = getColumnName(field);
        Class<?> type = field.getType();
        Integer columnLength = getColumnLength(field);
        boolean autoIncrement = isAutoIncrement(field);

        return new PrimaryKeyEntityColumn(field, columnName, type, columnLength, autoIncrement);
    }

    @Override
    public String toColumnDefinition(Dialect dialect) {
        StringJoiner definitionJoiner = new StringJoiner(" ");
        definitionJoiner.add(columnName);
        definitionJoiner.add(dialect.convertToSqlTypeDefinition(type, columnLength));
        if (autoIncrement) {
            definitionJoiner.add(dialect.autoIncrementDefinition());
        }
        definitionJoiner.add(dialect.primaryKeyDefinition());
        return definitionJoiner.toString();
    }

    @Override
    public boolean isPrimaryKeyField() {
        return true;
    }

    private static boolean isAutoIncrement(Field field) {
        GeneratedValue generatedValueAnnotation = field.getAnnotation(GeneratedValue.class);
        return generatedValueAnnotation != null && generatedValueAnnotation.strategy() == GenerationType.IDENTITY;
    }
}
