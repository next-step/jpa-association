package persistence.sql.column;

import persistence.sql.dialect.Dialect;
import persistence.sql.type.TableName;
import persistence.sql.type.NullableType;
import utils.CamelToSnakeCaseConverter;

import java.lang.reflect.Field;

public class GeneralColumn implements Column {

    private static final String DEFAULT_COLUMN_FORMAT = "%s %s";
    private static final String QUOTES = "'";

    private final TableName name;
    private Object value;
    private NullableType nullable;
    private final Field field;

    public GeneralColumn(Field field) {
        this.field = field;
        this.nullable = new NullableType();
        String columnName = getColumnNameWithColumn(field);
        this.name = new TableName(field.getName(), columnName);
    }

    public GeneralColumn(Object object, Field field) {
        this(field);
        this.value = getValue(object, field);
    }

    private Object getValue(Object entity, Field field) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }finally {
            field.setAccessible(false);
        }
    }

    private String getColumnNameWithColumn(Field field) {
        String columnName = field.getName();
        if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            boolean isNullable = field.getAnnotation(jakarta.persistence.Column.class).nullable();
            this.nullable = new NullableType(isNullable);
            columnName = field.getAnnotation(jakarta.persistence.Column.class).name();
        }
        return columnName;
    }

    @Override
    public String getDefinition(Dialect dialect) {
        return String.format(DEFAULT_COLUMN_FORMAT, name.getValue(),
                dialect.getColumnType(field.getType()).getColumnDefinition() + nullable.getDefinition());
    }

    @Override
    public String getName() {
        return CamelToSnakeCaseConverter.convert(name.getValue());
    }

    @Override
    public String getFieldName() {
        return name.getFieldName();
    }

    public Object getValue() {
        if (value instanceof String) {
            return QUOTES + value + QUOTES;
        }
        return value;
    }

    public boolean isAssociationEntity() {
        if(field.isAnnotationPresent(jakarta.persistence.ManyToOne.class) ||
                field.isAnnotationPresent(jakarta.persistence.OneToMany.class) ||
                field.isAnnotationPresent(jakarta.persistence.OneToOne.class) ||
                field.isAnnotationPresent(jakarta.persistence.ManyToMany.class)) {
            return true;
        }
        return false;
    }
}
