package persistence.sql.ddl.builder;

import java.util.List;

public class GeneralDDLResolverHolder {
    public static final List<GeneralAttributeDDLResolver> GENERAL_ATTRIBUTE_DDL_RESOLVERS = List.of(
            new LongTypeGeneralAttributeAttributeDDLResolver(),
            new IntegerTypeGeneralAttributeAttributeDDLResolver(),
            new StringTypeGeneralAttributeAttributeDDLResolver()
    );

    public static final List<IdAttributeDDLResolver> ID_ATTRIBUTE_DDL_RESOLVERS = List.of(
            new LongTypeIdAttributeDDLResolver(),
            new IntegerTypeIdAttributeDDLResolver(),
            new StringTypeIdAttributeDDLResolver()
    );
}
