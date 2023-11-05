package persistence.sql.ddl.builder;

import persistence.entity.attribute.EntityAttribute;
import persistence.sql.ddl.converter.SqlConverter;


public class DropDDLQueryBuilder implements DDLQueryBuilder {

    public DropDDLQueryBuilder() {
    }

    @Override
    public String prepareStatement(EntityAttribute entityAttribute, SqlConverter sqlConverter) {
        return String.format("DROP TABLE %s;", entityAttribute.getTableName());
    }
}
