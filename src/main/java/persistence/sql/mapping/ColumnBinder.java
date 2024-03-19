package persistence.sql.mapping;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import persistence.model.EntityMetaData;
import persistence.sql.QueryException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnBinder {

    private final ColumnTypeMapper columnTypeMapper;

    public ColumnBinder(ColumnTypeMapper columnTypeMapper) {
        this.columnTypeMapper = columnTypeMapper;
    }

    public List<Column> createColumns(final String tableName, final EntityMetaData metaData) {
        return metaData.getFields()
                .stream()
                .map(field -> this.createColumn(tableName, field))
                .collect(Collectors.toList());
    }

    public List<Column> createColumns(final String tableName, final EntityMetaData metaData, final Object object) {
        return metaData.getFields()
                .stream()
                .map(field -> createColumn(tableName, field, object))
                .collect(Collectors.toList());
    }

    public Column createColumn(final String tableName, final Field field) {
        final String columnName = toColumnName(field);
        final int sqlType = columnTypeMapper.toSqlType(field.getType());

        final Column column = getColumn(tableName, field, columnName, sqlType);

        final Id idAnnotation = field.getAnnotation(Id.class);

        if (idAnnotation != null) {
            column.setPk(true);

            final GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            if (generatedValue != null) {
                column.setStrategy(generatedValue.strategy());
            }
        }

        return column;
    }

    private Column getColumn(final String tableName, final Field field, final String columnName, final int sqlType) {
        final Value value = new Value(field.getType(), sqlType);

        final jakarta.persistence.Column columnAnnotation = field.getAnnotation(jakarta.persistence.Column.class);

        if (columnAnnotation != null) {
            int length = columnAnnotation.length();
            boolean nullable = columnAnnotation.nullable();
            boolean unique = columnAnnotation.unique();

            return new Column(tableName, columnName, sqlType, value, length, nullable, unique);
        }

        return new Column(tableName, columnName, sqlType, value);
    }

    private Column createColumn(final String tableName, final Field field, final Object object) {
        final Column column = createColumn(tableName, field);

        setColumnValue(column, field, object);

        return column;
    }

    public static String toColumnName(final Field field) {
        final jakarta.persistence.Column columnAnnotation = field.getAnnotation(jakarta.persistence.Column.class);

        if (columnAnnotation == null || columnAnnotation.name().isBlank()) {
            return field.getName();
        }

        return columnAnnotation.name();
    }

    private void setColumnValue(final Column column, final Field field, final Object object) {
        try {
            field.setAccessible(true);
            column.setValue(field.get(object));
        } catch (IllegalAccessException e) {
            throw new QueryException(column.getName() + " column set value exception");
        }

    }

}
