package persistence.core;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;

public interface EntityColumn {

    String ALIAS_DELIMITER = ".";

    String getName();

    boolean isNotNull();

    Class<?> getType();

    boolean isStringValued();

    int getStringLength();

    String getFieldName();

    boolean isInsertable();

    boolean isAutoIncrement();

    default boolean isId() {
        return this instanceof EntityIdColumn;
    }

    default boolean isOneToMany() {
        return this instanceof EntityOneToManyColumn;
    }

    default boolean isField() {
        return this instanceof EntityFieldColumn;
    }

    static EntityColumn from(final Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            return new EntityIdColumn(field);
        }

        if (field.isAnnotationPresent(OneToMany.class)) {
            return new EntityOneToManyColumn(field);
        }

        return new EntityFieldColumn(field);
    }

    default String getNameWithAlias(final String tableName) {
        return tableName + ALIAS_DELIMITER + this.getName();
    }
}
