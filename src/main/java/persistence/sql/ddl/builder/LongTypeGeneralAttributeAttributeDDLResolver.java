package persistence.sql.ddl.builder;

import persistence.entity.attribute.GeneralAttribute;
import persistence.entity.attribute.LongTypeGeneralAttribute;
import persistence.sql.ddl.converter.SqlConverter;

public class LongTypeGeneralAttributeAttributeDDLResolver implements GeneralAttributeDDLResolver {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == Long.class;
    }

    @Override
    public String prepareDDL(SqlConverter sqlConverter, GeneralAttribute generalAttribute) {
        if (!(generalAttribute instanceof LongTypeGeneralAttribute)) {
            throw new IllegalArgumentException("LongTypeGeneralAttribute 타입의 어트리뷰트만 들어올 수 있습니다.");
        }


        StringBuilder component = new StringBuilder();

        component.append(generalAttribute.getColumnName()).append(" ");
        component.append(sqlConverter.convert(Long.class));

        Integer scale = ((LongTypeGeneralAttribute) generalAttribute).getScale();
        boolean nullable = ((LongTypeGeneralAttribute) generalAttribute).isNullable();

        if (scale != 0) {
            component.append(String.format(" (%s)", scale));
        }

        if (!nullable) {
            component.append(" NOT NULL");
        }

        return component.toString().trim();
    }
}
