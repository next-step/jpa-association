package persistence.sql.ddl.builder;

import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.attribute.id.StringTypeIdAttribute;
import persistence.sql.ddl.converter.SqlConverter;

public class StringTypeIdAttributeDDLResolver implements IdAttributeDDLResolver {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public String prepareDDL(SqlConverter sqlConverter, IdAttribute idAttribute) {
        Integer length = ((StringTypeIdAttribute) idAttribute).getLength();

        String component = idAttribute.getColumnName() +
                " " + sqlConverter.convert(String.class) +
                String.format("(%s)", length) +
                " ";
        return component.trim();
    }
}
