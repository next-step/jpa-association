package persistence.sql.entity.model;

import jakarta.persistence.FetchType;

import java.lang.reflect.Field;

public interface DomainType {

    boolean isPrimaryDomain();

    boolean isEntityColumn();

    String getName();

    String getColumnName();

    String getValue();

    Class<?> getClassType();

    Field getField();

    boolean isJoinColumn();

    FetchType getFetchType();

    String getAlias(String tableName);
}
