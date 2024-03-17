package persistence.core;

import database.DatabaseVendor;
import jdbc.JdbcTemplate;
import persistence.sql.ddl.DDLQueryBuilder;
import persistence.sql.ddl.DDLQueryBuilderFactory;

public class DDLExcuteor {
    private final JdbcTemplate jdbcTemplate;
    private DDLQueryBuilder ddlQueryBuilder;
    private final EntityMetaManager entityMetaManager;

    public DDLExcuteor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        ddlQueryBuilder = DDLQueryBuilderFactory.getDDLQueryBuilder(DatabaseVendor.H2);
        entityMetaManager = EntityMetaManager.getInstance();
    }

    public void createTable(Class<?> clazz) {
        String sql = ddlQueryBuilder.createTableQuery(entityMetaManager.getEntityMetadata(clazz));
        jdbcTemplate.execute(sql);
    }

    public void dropTable(Class<?> clazz) {
        String sql = ddlQueryBuilder.dropTableQuery(entityMetaManager.getEntityMetadata(clazz));
        jdbcTemplate.execute(sql);
    }

}
