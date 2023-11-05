package persistence.sql.ddl.builder;

import persistence.entity.attribute.id.IdAttribute;
import persistence.sql.ddl.converter.SqlConverter;

public interface IdAttributeDDLResolver {
    boolean supports(Class<?> clazz);

    String prepareDDL(SqlConverter sqlConverter, IdAttribute idAttribute);
}
