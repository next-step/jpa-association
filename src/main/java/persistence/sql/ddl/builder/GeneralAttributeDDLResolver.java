package persistence.sql.ddl.builder;

import persistence.entity.attribute.GeneralAttribute;
import persistence.sql.ddl.converter.SqlConverter;

public interface GeneralAttributeDDLResolver {
    boolean supports(Class<?> clazz);

    String prepareDDL(SqlConverter sqlConverter, GeneralAttribute generalAttribute);
}
