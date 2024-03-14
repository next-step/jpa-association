package persistence.sql.column;

import jakarta.persistence.JoinColumn;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;

public class JoinEntityColumn implements Column {

    private final ColumnNameProperty columnNameProperty;

    public JoinEntityColumn(Field field) {
        this.columnNameProperty = new ColumnNameProperty(getJoinField(field), field.getName());
    }

    private String getJoinField(Field joinField) {
        JoinColumn joinColumn = joinField.getDeclaredAnnotation(JoinColumn.class);
        if (joinColumn != null && !joinColumn.name().isEmpty()) {
            return joinColumn.name();
        }
        return joinField.getName();
    }

    @Override
    public String getDefinition(Dialect dialect) {
        return null;
    }

    @Override
    public String getName() {
        return columnNameProperty.getColumnName();
    }

    public String getFieldName() {
        return columnNameProperty.getFieldName();
    }

    @Override
    public Field getField() {
        throw new UnsupportedOperationException("Not supported");
    }

}
