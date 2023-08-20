package persistence.sql.ddl.builder;

import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;
import persistence.sql.ddl.column.DdlColumn;

public class DdlQueryBuilder {
    private static final String CREATE_QUERY_FORMAT = "create table %s (%s)";
    private static final String DROP_QUERY_FORMAT = "drop table %s";

    private final String tableName;
    private final ColumnBuilder ddlColumnBuilder;

    public DdlQueryBuilder(Class<?> tableClazz) {
        this(tableClazz, new ColumnBuilder(DdlColumn.ofList(EntityMetaFactory.INSTANCE.create(tableClazz))));
    }

    private DdlQueryBuilder(Class<?> tableClazz, ColumnBuilder ddlColumnBuilder) {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(tableClazz);
        this.tableName = entityMeta.getTableName();
        this.ddlColumnBuilder = ddlColumnBuilder;
    }

    public String create() {
        return String.format(CREATE_QUERY_FORMAT, tableName, ddlColumnBuilder.build());
    }

    public String drop() {
        return String.format(DROP_QUERY_FORMAT, tableName);
    }
}
