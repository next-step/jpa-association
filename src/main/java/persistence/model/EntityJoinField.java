package persistence.model;

import persistence.sql.mapping.ColumnBinder;

import java.lang.reflect.Field;

public class EntityJoinField extends AbstractEntityField {

    private final String joinedColumnName;
    private boolean lazy;

    protected EntityJoinField(final String fieldName, final String columnName, final Class<?> entityClass, final Field field) {
        super(fieldName, columnName, entityClass, field);
        this.joinedColumnName = ColumnBinder.toJoinColumnName(field);
    }

    public void setLazy(final boolean lazy) {
        this.lazy = lazy;
    }

    public boolean isEager() {
        return !lazy;
    }

    public String getJoinedColumnName() {
        return this.joinedColumnName;
    }
}
