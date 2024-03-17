package persistence.sql.entity.model;

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

    String getAcronyms(String tableName);
}
