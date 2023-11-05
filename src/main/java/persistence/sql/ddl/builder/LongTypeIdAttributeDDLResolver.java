package persistence.sql.ddl.builder;

import persistence.entity.attribute.id.IdAttribute;
import persistence.sql.ddl.converter.SqlConverter;

public class LongTypeIdAttributeDDLResolver implements IdAttributeDDLResolver {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == Long.class;
    }

    @Override
    public String prepareDDL(SqlConverter sqlConverter, IdAttribute idAttribute) {
        String component = idAttribute.getColumnName() + " " +
                sqlConverter.convert(Long.class) + " " +
                sqlConverter.convert(idAttribute.getGenerationType().getClass());
        return component.trim();
    }
}
