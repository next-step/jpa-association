package persistence.entity;

import java.util.List;
import jdbc.JdbcTemplate;
import jdbc.ResultMapper;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.JoinColumn;
import persistence.sql.common.meta.TableName;
import persistence.sql.dml.Query;

public class EntityLoader<T> {

    private final Query query;
    private final JdbcTemplate jdbcTemplate;
    private final ResultMapper<T> resultMapper;
    private final TableName tableName;
    private final Columns columns;
    private final JoinColumn joinColumn;

    EntityLoader(JdbcTemplate jdbcTemplate, Class<T> tClass, Query query) {
        this.query = query;

        this.jdbcTemplate = jdbcTemplate;
        this.resultMapper = new ResultMapper<>(tClass);

        this.tableName = TableName.of(tClass);
        this.columns = Columns.of(tClass.getDeclaredFields());
        this.joinColumn = JoinColumn.of(tClass.getDeclaredFields());
    }

    public List<T> findAll() {
        String q = query.selectAll(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns);

        return jdbcTemplate.query(q, resultMapper);
    }

    public <I> T findById(I input) {
        EntityMeta entityMeta = new EntityMeta(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns, joinColumn);

        String selectQuery = query.select(entityMeta, input);

        return jdbcTemplate.queryForObject(selectQuery, resultMapper);
    }

    public <I> int getHashCode(I input) {
        EntityMeta entityMeta = EntityMeta.selectMeta("findById", tableName, columns, joinColumn);

        return query.select(entityMeta, input).hashCode();
    }
}
