package persistence.sql.ddl.builder;

import persistence.entity.attribute.GeneralAttribute;
import persistence.entity.attribute.StringTypeGeneralAttribute;
import persistence.sql.ddl.converter.SqlConverter;

public class StringTypeGeneralAttributeAttributeDDLResolver implements GeneralAttributeDDLResolver {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public String prepareDDL(SqlConverter sqlConverter, GeneralAttribute generalAttribute) {
        if (!(generalAttribute instanceof StringTypeGeneralAttribute)) {
            throw new IllegalArgumentException("LongTypeGeneralAttribute 타입의 어트리뷰트만 들어올 수 있습니다.");
        }

        Integer length = ((StringTypeGeneralAttribute) generalAttribute).getLength();
        boolean nullable = ((StringTypeGeneralAttribute) generalAttribute).isNullable();

        String component = generalAttribute.getColumnName() + " " +
                sqlConverter.convert(String.class) +
                String.format("(%s)", length) + (!nullable ? " NOT NULL" : "");
        return component.trim();
    }
}
