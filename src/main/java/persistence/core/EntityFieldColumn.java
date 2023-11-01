package persistence.core;

import jakarta.persistence.Column;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

public class EntityFieldColumn implements EntityColumn {
    private final String name;
    private final String fieldName;
    private final Class<?> type;
    private final boolean isNotNull;
    private final boolean isStringValued;
    private final int stringLength;
    private final boolean isInsertable;


    public EntityFieldColumn(final Field field) {
        field.setAccessible(true);
        this.name = initName(field);
        this.fieldName = field.getName();
        this.type = field.getType();
        this.isNotNull = initIsNotNull(field);
        this.isStringValued = this.type.isAssignableFrom(String.class);
        this.stringLength = initStringLength(field);
        this.isInsertable = initIsInsertable(field);
    }

    private String initName(final Field field) {
        final Column columnMetadata = field.getDeclaredAnnotation(Column.class);
        return Optional.ofNullable(columnMetadata)
                .filter(column -> !column.name().isEmpty())
                .map(Column::name)
                .orElse(field.getName());
    }

    private boolean initIsNotNull(final Field field) {
        final Column columnMetadata = field.getDeclaredAnnotation(Column.class);
        return Optional.ofNullable(columnMetadata)
                .map(column -> !column.nullable())
                .orElse(false);
    }

    private int initStringLength(final Field field) {
        final Column columnMetadata = field.getDeclaredAnnotation(Column.class);
        return Optional.ofNullable(columnMetadata)
                .map(Column::length)
                .orElse(255);
    }

    private boolean initIsInsertable(final Field field) {
        final Column columnMetadata = field.getDeclaredAnnotation(Column.class);
        return Optional.ofNullable(columnMetadata)
                .map(Column::insertable)
                .orElse(true);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isNotNull() {
        return this.isNotNull;
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    @Override
    public boolean isStringValued() {
        return this.isStringValued;
    }

    @Override
    public int getStringLength() {
        return this.stringLength;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public boolean isInsertable() {
        return this.isInsertable;
    }

    @Override
    public boolean isAutoIncrement() {
        return false;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final EntityFieldColumn that = (EntityFieldColumn) object;
        return isNotNull == that.isNotNull && isStringValued == that.isStringValued && stringLength == that.stringLength && isInsertable == that.isInsertable && Objects.equals(name, that.name) && Objects.equals(fieldName, that.fieldName) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fieldName, type, isNotNull, isStringValued, stringLength, isInsertable);
    }
}
