package persistence;

import builder.dml.EntityData;
import builder.dml.builder.DeleteQueryBuilder;
import builder.dml.builder.InsertQueryBuilder;
import builder.dml.builder.UpdateQueryBuilder;
import jdbc.JdbcTemplate;

public class EntityPersister {

    private final static String DATA_NOT_EXIST_MESSAGE = "데이터가 존재하지 않습니다. : ";
    private final JdbcTemplate jdbcTemplate;

    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //데이터를 반영한다.
    public void persist(EntityData EntityData) {
        jdbcTemplate.execute(insertQueryBuilder.buildQuery(EntityData));
    }

    //데이터를 수정한다.
    public void merge(EntityData EntityData) {
        jdbcTemplate.execute(updateQueryBuilder.buildQuery(EntityData));
    }

    //데이터를 제거한다.
    public void remove(EntityData EntityData) {
        jdbcTemplate.execute(deleteQueryBuilder.buildQuery(EntityData));
    }

}
