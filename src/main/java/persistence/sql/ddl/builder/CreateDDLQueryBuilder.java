package persistence.sql.ddl.builder;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.id.IdAttribute;
import persistence.sql.ddl.converter.SqlConverter;

import java.util.stream.Collectors;

import static persistence.sql.ddl.builder.GeneralDDLResolverHolder.GENERAL_ATTRIBUTE_DDL_RESOLVERS;
import static persistence.sql.ddl.builder.GeneralDDLResolverHolder.ID_ATTRIBUTE_DDL_RESOLVERS;

public class CreateDDLQueryBuilder implements DDLQueryBuilder {
    public CreateDDLQueryBuilder() {
    }

    @Override
    public String prepareStatement(EntityAttribute entityAttribute, SqlConverter sqlConverter) {
        String generalAttributesDDL = entityAttribute.getGeneralAttributes().stream()
                .map(generalAttribute -> {
                    for (GeneralAttributeDDLResolver generalDDLResolver : GENERAL_ATTRIBUTE_DDL_RESOLVERS) {
                        if (generalDDLResolver.supports(generalAttribute.getField().getType())) {
                            return generalDDLResolver.prepareDDL(sqlConverter, generalAttribute);
                        }
                    }
                    throw new IllegalArgumentException(String.format("[%s] 알수없는 타입의 generalAttribute 입니다.",
                            generalAttribute.getField().getType()));
                })
                .collect(Collectors.joining(", "));

        IdAttribute idAttribute = entityAttribute.getIdAttribute();

        String idAttributeDDL = ID_ATTRIBUTE_DDL_RESOLVERS.stream()
                .filter(idAttributeDDLResolver -> idAttributeDDLResolver.supports(idAttribute.getField().getType()))
                .map(idAttributeDDLResolver -> idAttributeDDLResolver.prepareDDL(sqlConverter, idAttribute))
                .findFirst()
                .orElseThrow();

        return String.format("CREATE TABLE %s ( %s );", entityAttribute.getTableName(),
                idAttributeDDL + (generalAttributesDDL.isBlank() ? "" : ", " + generalAttributesDDL));
    }
}
