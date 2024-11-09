package persistence.sql.entity;

import jakarta.persistence.FetchType;
import persistence.sql.model.TableName;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

public class EntityJoinInfo {

    private final String joinTableName;
    private final String columnName;
    private final String joinColumnName;
    private final Class<?> joinClazz;
    private final Class<?> clazz;
    private final Field joinField;
    private final FetchType fetchType;

    public EntityJoinInfo(Class<?> joinClazz, Class<?> clazz, String columnName, FetchType fetchType, String joinColumnName, Field joinField) {
        this.joinClazz = joinClazz;
        TableName joinTableName = new TableName(joinClazz);
        this.joinTableName = joinTableName.getValue();
        this.columnName = columnName;
        this.joinColumnName = joinColumnName;
        this.clazz = clazz;
        this.joinField = joinField;
        this.fetchType = fetchType;
    }


    public String getJoinColumnName() {
        return joinColumnName;
    }

    public String getJoinTableName() {
        return joinTableName;
    }

    public Class<?> getJoinClazz() {
        return this.joinClazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getColumnName() {
        return columnName;
    }

    public Collection<?> getEntityJoinCollections(Object entityObject) {
        try {
            this.joinField.setAccessible(true);
            return (Collection<?>) this.joinField.get(entityObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
