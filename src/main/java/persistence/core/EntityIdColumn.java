package persistence.core;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

public class EntityIdColumn implements EntityColumn {
    private final EntityColumn column;
    private final boolean isAutoIncrement;

    public EntityIdColumn(final Field field, final String tableName) {
        field.setAccessible(true);
        this.column = new EntityFieldColumn(field, tableName);
        this.isAutoIncrement = initIsAutoIncrement(field);
    }

    private boolean initIsAutoIncrement(final Field field) {
        final GeneratedValue generatedValue = field.getDeclaredAnnotation(GeneratedValue.class);
        return Optional.ofNullable(generatedValue)
                .filter(value -> value.strategy() == GenerationType.IDENTITY)
                .isPresent();
    }

    @Override
    public String getTableName() {
        return this.column.getTableName();
    }

    @Override
    public String getName() {
        return this.column.getName();
    }

    @Override
    public boolean isNotNull() {
        return true;
    }

    @Override
    public Class<?> getType() {
        return this.column.getType();
    }

    @Override
    public boolean isStringValued() {
        return this.column.isStringValued();
    }

    @Override
    public int getStringLength() {
        return this.column.getStringLength();
    }

    @Override
    public String getFieldName() {
        return this.column.getFieldName();
    }

    @Override
    public boolean isInsertable() {
        return false;
    }

    @Override
    public boolean isAutoIncrement() {
        return this.isAutoIncrement;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final EntityIdColumn that = (EntityIdColumn) object;
        return isAutoIncrement == that.isAutoIncrement && Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, isAutoIncrement);
    }
}
