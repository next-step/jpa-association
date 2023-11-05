package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectFactory;
import persistence.sql.dml.DmlQueryGenerator;
import persistence.sql.meta.ColumnMetas;
import persistence.sql.meta.EntityMeta;
import persistence.sql.meta.MetaFactory;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryGenerator dmlQueryGenerator;

    private EntityLoader(JdbcTemplate jdbcTemplate, DmlQueryGenerator dmlQueryGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryGenerator = dmlQueryGenerator;
    }

    public static EntityLoader of(JdbcTemplate jdbcTemplate) {
        DialectFactory dialectFactory = DialectFactory.getInstance();
        Dialect dialect = dialectFactory.getDialect(jdbcTemplate.getDbmsName());
        DmlQueryGenerator dmlQueryGenerator = DmlQueryGenerator.of(dialect);
        return new EntityLoader(jdbcTemplate, dmlQueryGenerator);
    }

    public <T> T selectOne(Class<T> clazz, Long id) {
        String selectByPkQuery = dmlQueryGenerator.generateSelectByPkQuery(clazz, id);
        T entity = jdbcTemplate.queryForObject(selectByPkQuery, new EntityRowMapper<>(clazz));
        EntityMeta entityMeta = MetaFactory.get(clazz);
        ColumnMetas columnMetas = entityMeta.getColumnMetas();
        if (columnMetas.hasJoinEntity()) {
            setJoinTargets(entity, id);
        }
        return entity;
    }

    private <T> void setJoinTargets(T entity, Long id) {
        String selectQuery = dmlQueryGenerator.generateSelectWithJoinByPkQuery(entity.getClass(), id);
        EntityMeta entityMeta = MetaFactory.get(entity.getClass());
        ColumnMetas columnMetas = entityMeta.getColumnMetas();
        columnMetas.forEach(columnMeta -> {
            if (columnMeta.isJoinColumn()) {
                // Join table 이름으로 자식 엔티티 목록을 매핑조회한다
                // 조회한 인스턴스를 부모 인스턴스에 세팅한다
            }
        });
    }
}
